attribute vec2 a_Position;

uniform mat4 u_ViewProjectionMatrix;
uniform float u_Zoom;
uniform mat4 u_RotationMatrix;

varying vec3 v_Position;

void main()
{
    vec4 position;
    float pi = 3.14159265359;
    float phi = radians(a_Position.x);
    float theta = radians(a_Position.y);

    // We first translate from longitude/latitudes into xyz coordinates
    position.x = -cos(theta) * sin(phi);
    position.y = sin(theta);
    position.z = cos(theta) * cos(phi);
    position.w = 1.0;

    // then apply the rotation
    vec4 positionNew = u_RotationMatrix * position;
    v_Position = positionNew.xyz;

    // and finally scale according to the zoom in the xy-directions and apply the View-Projection
    gl_Position = u_ViewProjectionMatrix * vec4(positionNew.xy * u_Zoom, positionNew.zw);
}
