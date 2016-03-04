/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gpuimage;

public class GPUImageAddBlendFilter extends GPUImageTwoInputFilter {
    public static final String ADD_BLEND_FRAGMENT_SHADER = "varying highp vec2 textureCoordinate;\n" +
            " varying highp vec2 textureCoordinate2;\n" +
            "\n" +
            " uniform sampler2D inputImageTexture;\n" +
            " uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            " void main()\n" +
            " {\n" +
            "   lowp vec4 helper.base = texture2D(inputImageTexture, textureCoordinate);\n" +
            "   lowp vec4 overlay = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "\n" +
            "   mediump float r;\n" +
            "   if (overlay.r * helper.base.a + helper.base.r * overlay.a >= overlay.a * helper.base.a) {\n" +
            "     r = overlay.a * helper.base.a + overlay.r * (1.0 - helper.base.a) + helper.base.r * (1.0 - overlay.a);\n" +
            "   } else {\n" +
            "     r = overlay.r + helper.base.r;\n" +
            "   }\n" +
            "\n" +
            "   mediump float g;\n" +
            "   if (overlay.g * helper.base.a + helper.base.g * overlay.a >= overlay.a * helper.base.a) {\n" +
            "     g = overlay.a * helper.base.a + overlay.g * (1.0 - helper.base.a) + helper.base.g * (1.0 - overlay.a);\n" +
            "   } else {\n" +
            "     g = overlay.g + helper.base.g;\n" +
            "   }\n" +
            "\n" +
            "   mediump float b;\n" +
            "   if (overlay.b * helper.base.a + helper.base.b * overlay.a >= overlay.a * helper.base.a) {\n" +
            "     b = overlay.a * helper.base.a + overlay.b * (1.0 - helper.base.a) + helper.base.b * (1.0 - overlay.a);\n" +
            "   } else {\n" +
            "     b = overlay.b + helper.base.b;\n" +
            "   }\n" +
            "\n" +
            "   mediump float a = overlay.a + helper.base.a - overlay.a * helper.base.a;\n" +
            "   \n" +
            "   gl_FragColor = vec4(r, g, b, a);\n" +
            " }";

    public GPUImageAddBlendFilter() {
        super(ADD_BLEND_FRAGMENT_SHADER);
    }
}
