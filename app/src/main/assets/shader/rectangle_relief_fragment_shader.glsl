precision highp float;

uniform vec4 u_Color;
uniform mat4 u_RotationMatrix;
uniform samplerCube u_CubeMap;
uniform sampler2D u_Texture;
varying vec3 v_Position;
uniform bool u_directRendering;
uniform float u_Zoom;

void main() {

    if(u_directRendering) {

        float r = length(v_Position.xy);
        vec4 position = u_RotationMatrix * vec4(v_Position.x, v_Position.y, sqrt(1.-r*r), 1.);
        vec3 npos = position.xyz;

        float pi = 3.14159265359;
        float twopi = 2.*pi;

        float theta = asin(npos.y);

        float phi1 = acos(npos.z / cos(theta));
        float dist1 = abs(npos.x + cos(theta) * sin(phi1));

        float phi2 = twopi - phi1;
        float dist2 = abs(npos.x + cos(theta) * sin(phi2));

        float phi3 = asin(-npos.x / cos(theta));
        float dist3 = abs(npos.z - cos(theta) * cos(phi3));

        float phi4 = pi - phi3;
        float dist4 = abs(npos.z - cos(theta) * cos(phi4));

        float phi = phi1;
        if (dist1 > dist2) {
            phi = phi2;
            dist1 = dist2;
        }
        if (dist1 > dist3) {
            phi = phi3;
            dist1 = dist3;
        }
        if (dist1 > dist4) phi = phi4;
        if (phi < 0.) phi = phi + twopi;

        float norm_phi = .5 + phi / twopi;
        float norm_theta = .5 - theta / pi;
        vec4 relief = texture2D(u_Texture, vec2(norm_phi, norm_theta));

        float temp = u_Zoom - 4.;
        temp = max(temp / 10., 0.);
        relief.a = .5 + relief.a / 2.;
        relief.a = relief.a + temp;
        relief.a = min(relief.a, 1.);

        vec4 color = vec4(u_Color.rgb * relief.a, u_Color.a);

        // perform shading
        vec3 lightPosition = vec3(1., 1., 5.);
        vec3 posNorm = normalize(vec3(v_Position.x, v_Position.y, sqrt(1.-r*r)));
        vec3 lightDir = normalize(lightPosition - posNorm);
        float diff = max(dot(lightDir, posNorm), .0) + .3;

        color.xyz = diff * color.xyz;
        gl_FragColor = color;
    }
    else {

        vec3 npos = normalize(v_Position);

        float pi = 3.14159265359;
        float twopi = 2.*pi;

        float theta = asin(npos.y);

        float phi1 = acos(npos.z / cos(theta));
        float dist1 = abs(npos.x + cos(theta) * sin(phi1));

        float phi2 = twopi - phi1;
        float dist2 = abs(npos.x + cos(theta) * sin(phi2));

        float phi3 = asin(-npos.x / cos(theta));
        float dist3 = abs(npos.z - cos(theta) * cos(phi3));

        float phi4 = pi - phi3;
        float dist4 = abs(npos.z - cos(theta) * cos(phi4));

        float phi = phi1;
        if (dist1 > dist2) {
            phi = phi2;
            dist1 = dist2;
        }
        if (dist1 > dist3) {
            phi = phi3;
            dist1 = dist3;
        }
        if (dist1 > dist4) phi = phi4;
        if (phi < 0.) phi = phi + twopi;

        float norm_phi = - phi / twopi;
        float norm_theta = .5 - theta / pi;
        vec4 relief = texture2D(u_Texture, vec2(norm_phi, norm_theta));

        relief.a = .5 + relief.a / 2.;

        gl_FragColor = vec4(u_Color.rgb * relief.a, u_Color.a);
    }
}
