attribute vec3 a_Position;

uniform mat4 u_RotationMatrix;

uniform mat4 u_ViewProjectionMatrix;
uniform float u_Zoom;
uniform bool u_directRendering;

varying vec3 v_Position;

void main()                    
{
    if(u_directRendering) {
        v_Position = a_Position;
        gl_Position = u_ViewProjectionMatrix * vec4(a_Position.xy * u_Zoom, a_Position.z, 1.0);
    }
    else {
        vec4 newPos = u_ViewProjectionMatrix * u_RotationMatrix * vec4(a_Position, 1.);
        v_Position = newPos.xyz;
        gl_Position = vec4(a_Position, 1.);
    }
}