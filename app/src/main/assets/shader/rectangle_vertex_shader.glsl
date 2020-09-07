attribute vec3 a_Position;

uniform mat4 u_ViewProjectionMatrix;
uniform float u_Zoom;

varying vec3 v_Position;

void main()                    
{
    v_Position = a_Position;
    gl_Position = u_ViewProjectionMatrix * vec4(a_Position.xy * u_Zoom, a_Position.z, 1.0);
}