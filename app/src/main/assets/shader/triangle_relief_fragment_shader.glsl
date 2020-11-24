precision highp float;

uniform vec4 u_Color;
uniform bool u_directRendering;
uniform sampler2D u_Texture;

varying vec3 v_Position;
varying vec2 v_PositionAngles;
uniform float u_Zoom;

void main() {

    float pi = 3.14159265359;
    float twopi = 2.*pi;

    float norm_phi = .5 + v_PositionAngles.x / twopi;
    float norm_theta = .5 - v_PositionAngles.y / pi;
    vec4 relief = texture2D(u_Texture, vec2(norm_phi, norm_theta));
    float temp = u_Zoom - 4.;
    temp = max(temp / 10., 0.);
    relief.a = 1.5 - relief.a;
    relief.a = clamp(relief.a, 0., 1.);
    relief.a = relief.a + temp;
    relief.a = min(relief.a, 1.);

    if(!u_directRendering) {
        // If we are rendering to a cubemap then the lighting is performed in another shader

        gl_FragData[0] = vec4(u_Color.rgb * relief.a, u_Color.a);
    }
    else {
        // If the triangles are rendered onto the screen directly then
        // we perform simple ambient lighting
        vec3 lightPosition = vec3(1., 1., 5.);
        float r = length(v_Position.xy);
        vec3 posNorm = normalize(vec3(v_Position.x, v_Position.y, sqrt(1.-r*r)));
        vec3 lightDir = normalize(lightPosition - posNorm);
        float diff = max(dot(lightDir, posNorm), .0) + .3;

        gl_FragData[0] = vec4(u_Color.rgb * diff * relief.a, u_Color.a * diff);
    }
}
