precision highp float;

uniform vec4 u_Color;
uniform mat4 u_RotationMatrix;
uniform float u_Zoom;

uniform bool u_directRendering;

varying vec3 v_Position;

uniform samplerCube u_CubeMap;

void main()
{
    float r = length(v_Position.xy);
    vec4 position = u_RotationMatrix * vec4(v_Position.x, v_Position.y, sqrt(1.-r*r), 1.);

    if(r < 1.){

        vec4 color;

        if(u_directRendering) color = u_Color;
        else color = textureCube(u_CubeMap, position.xyz);

        // perform shading
        vec3 lightPosition = vec3(1., 1., 5.);
        vec3 posNorm = normalize(vec3(v_Position.x, v_Position.y, sqrt(1.-r*r)));
        vec3 lightDir = normalize(lightPosition - posNorm);
        float diff = max(dot(lightDir, posNorm), .0) + .3;

        color.xyz = diff * color.xyz;
        gl_FragColor = color;
    }
    else gl_FragColor = u_Color;
}
