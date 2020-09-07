attribute vec2 a_Position;
attribute vec2 a_Direction;

uniform mat4 u_ViewProjectionMatrix;
uniform mat4 u_RotationMatrix;
uniform float u_Zoom;
uniform float u_Thickness;
uniform bool u_directRendering;

varying vec3 v_Position;
varying vec3 v_mappedPosition;
varying vec3 v_mappedCenterPosition;
varying float v_Radius;

void main() {

  vec4 zoom_vec = vec4(u_Zoom, u_Zoom, 1.0, 1.0);

  float pi = 3.14159265359;
  float phi = radians(a_Position.x);
  float theta = radians(a_Position.y);

  // We first translate from longitude/latitudes into xyz coordinates
  vec4 position;
  position.x = -cos(theta) * sin(phi);
  position.y = sin(theta);
  position.z = cos(theta) * cos(phi);
  position.w = 1.0;

  v_Position = (u_RotationMatrix * position).xyz;

  phi = radians(a_Direction.x);
  theta = radians(a_Direction.y);

  // We first translate from longitude/latitudes into xyz coordinates
  vec4 direction;
  direction.x = -cos(theta) * sin(phi);
  direction.y = sin(theta);
  direction.z = cos(theta) * cos(phi);
  direction.w = 1.0;

  vec4 currentRotated = (u_RotationMatrix * position) * zoom_vec;
  vec4 directionRotated = (u_RotationMatrix * direction) * zoom_vec;

  vec3 dir = normalize((directionRotated - currentRotated).xyz);

  vec3 outward = vec3(0., 0., 1.);
  if(!u_directRendering) outward = normalize(currentRotated.xyz);

  vec3 normal = cross(dir, outward) * u_Zoom * u_Thickness;

  vec4 offset = vec4(normal, 0.0);

  v_mappedPosition = (u_ViewProjectionMatrix * (currentRotated + offset)).xyz;
  v_mappedCenterPosition = (u_ViewProjectionMatrix * currentRotated).xyz;
  v_Radius = length(v_mappedCenterPosition - v_mappedPosition) / sqrt(2.);

  gl_Position = u_ViewProjectionMatrix * (currentRotated + offset);
}
