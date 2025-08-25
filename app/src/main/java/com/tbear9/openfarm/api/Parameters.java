package com.tbear9.openfarm.api;

import lombok.Builder;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public interface Parameters extends Serializable {
    @Serial long serialVersionUID = 2025L;
    Map<String, String> getParameters();

    @Builder
    @Setter
    public static class SoilParameters implements Parameters {

        public E.DEPTH O_depth;
        public E.TEXTURE O_texture;
        public E.FERTILITY O_fertility; // tingkat kesuburan
        public E.DRAINAGE O_drainage;
        public float pH;

        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.O_soil_depth, O_depth== null? null : O_depth.head);
            map.put(E.O_soil_texture, O_texture== null? null : O_texture.head);
            map.put(E.O_soil_fertility, O_fertility== null? null : O_fertility.head);
            map.put(E.O_soil_drainage, O_drainage== null? null : O_drainage.head);
            map.put("PH", String.valueOf(pH));

            return map;
        }
    }

    @Builder
    public static class GeoParameters implements Parameters {
        private final E.CLIMATE iklim;
        private final int latitude;
//        private final int longitude;
        private final int altitude;
        private final int rainfall;
        private final int temperature;

        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.Climate_zone, iklim == null? null : iklim.head);
            map.put("LAT", String.valueOf(latitude));
//            map.put("LONG", String.valueOf(longitude));
            if(altitude == 0) throw new IllegalArgumentException("Altitude cannot be zero");
            map.put("ALT", String.valueOf(altitude));
            if(rainfall == 0) throw new IllegalArgumentException("Rainfall cannot be zero");
            map.put("RAIN", String.valueOf(rainfall));
            map.put("TEMP", String.valueOf(temperature));
            return map;
        }
    }

    @Builder
    public static class UserParameters implements Parameters {
        private final E.CATEGORY category;
        private final E.LIFESPAM lifeSpan;
        private final String query;
        private final int panen;
        @Override
        public Map<String, String> getParameters() {
            Map<String, String> map = new HashMap<>();
            map.put(E.Category, category == null ? null : category.head);
            map.put(E.Life_span, lifeSpan == null ? null : lifeSpan.head);
            map.put("PANEN", String.valueOf(panen));
            map.put("QUERY", query);
            return map;
        }
    }
}
