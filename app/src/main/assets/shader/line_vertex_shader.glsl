attribute vec2 a_Position;
attribute float a_Direction;
attribute vec2 a_Next;
attribute vec2 a_Previous;

uniform mat4 u_ViewProjectionMatrix;
uniform mat4 u_RotationMatrix;
uniform float u_Zoom;
uniform float u_Thickness;
uniform bool u_directRendering;

varying vec3 v_Position;

void main() {

  vec4 zoom_vec = vec4(u_Zoom, u_Zoom, 1.0, 1.0);

  vec4 position;
  float pi = 3.14159265359;
  float phi = radians(a_Position.x);
  float theta = radians(a_Position.y);

  // We first translate from longitude/latitudes into xyz coordinates
  position.x = -cos(theta) * sin(phi);
  position.y = sin(theta);
  position.z = cos(theta) * cos(phi);
  position.w = 1.0;

  v_Position = (u_RotationMatrix * position).xyz;

  vec4 previous;
  phi = radians(a_Previous.x);
  theta = radians(a_Previous.y);

  // We first translate from longitude/latitudes into xyz coordinates
  previous.x = -cos(theta) * sin(phi);
  previous.y = sin(theta);
  previous.z = cos(theta) * cos(phi);
  previous.w = 1.0;

  vec4 next;
  phi = radians(a_Next.x);
  theta = radians(a_Next.y);

  // We first translate from longitude/latitudes into xyz coordinates
  next.x = -cos(theta) * sin(phi);
  next.y = sin(theta);
  next.z = cos(theta) * cos(phi);
  next.w = 1.0;

  vec4 previousRotated = (u_RotationMatrix * previous) * zoom_vec;
  vec4 currentRotated = (u_RotationMatrix * position) * zoom_vec;
  vec4 nextRotated = (u_RotationMatrix * next) * zoom_vec;

  vec3 outward = vec3(0., 0., 1.);
  if(!u_directRendering) outward = normalize(currentRotated.xyz);

  float len = u_Thickness;
  float orientation = a_Direction;

  //starting point uses (next - current)
  vec3 dir = vec3(0.0, 0.0, 0.0);

  if (a_Position == a_Previous) {
    dir = normalize(nextRotated.xyz - currentRotated.xyz);
  } 
  //ending point uses (current - previous)
  else if (a_Position == a_Next) {
    dir = normalize(currentRotated.xyz - previousRotated.xyz);
  }
  //somewhere in middle, needs a join
  else {
    //get directions from (C - B) and (B - A)
    vec3 dirA = normalize(currentRotated.xyz - previousRotated.xyz);
    vec3 dirB = normalize(nextRotated.xyz - currentRotated.xyz);

    vec3 tangent = normalize(dirA + dirB);
    vec3 perp = cross(dirA, outward);
    vec3 miter = cross(tangent, outward);
    dir = tangent;

    float miterScale = dot(miter, perp);

    if(miterScale > .1) {
      len = u_Thickness / dot(miter, perp);
    }
  }

  vec3 normal = cross(dir, outward);
  normal *= u_Zoom * len;

  vec4 offset = vec4(normal * orientation, 0.0);
  gl_Position = u_ViewProjectionMatrix * (currentRotated + offset);
}
