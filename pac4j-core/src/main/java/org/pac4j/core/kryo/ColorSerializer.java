/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.*;
import org.pac4j.core.profile.Color;

/**
 * This class is a Kryo serializer for {@link Color}.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class ColorSerializer extends Serializer<Color> {

    private final IntSerializer intSerializer = new IntSerializer();

    @Override
    public Color read(Kryo kryo, Input input, Class<Color> aClass) {
        final int red = this.intSerializer.read(kryo, input, Integer.class);
        final int green = this.intSerializer.read(kryo, input, Integer.class);
        final int blue = this.intSerializer.read(kryo, input, Integer.class);
        return new Color(red, green, blue);
    }

    @Override
    public void write(Kryo kryo, Output output, Color color) {
        this.intSerializer.write(kryo, output, color.getRed());
        this.intSerializer.write(kryo, output, color.getGreen());
        this.intSerializer.write(kryo, output, color.getBlue());
    }

}
