precision highp float;

uniform vec4 u_Color;
uniform bool u_directRendering;

varying vec3 v_Position;

void main()
{
    if(!u_directRendering) {
        // If we are rendering to a cubemap then the lighting is performed in another shader
        gl_FragData[0] = u_Color;
    }
    else {
        // If the triangles are rendered onto the screen directly then
        // we perform simple ambient lighting
        vec3 lightPosition = vec3(1., 1., 5.);
        float r = length(v_Position.xy);
        vec3 posNorm = normalize(vec3(v_Position.x, v_Position.y, sqrt(1.-r*r)));
        vec3 lightDir = normalize(lightPosition - posNorm);
        float diff = max(dot(lightDir, posNorm), .0) + .3;

        gl_FragData[0] = u_Color * diff;
    }
}
