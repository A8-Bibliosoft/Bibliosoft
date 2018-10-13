package com.lib.bibliosoft.entity;

import javax.persistence.*;

@Entity
@Table(name="defsetting")
public class DefSetting {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        private String deftype;

        private Integer defnumber;

        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getDeftype() {
                return deftype;
        }

        public void setDeftype(String deftype) {
                this.deftype = deftype;
        }

        public Integer getDefnumber() {
                return defnumber;
        }

        public void setDefnumber(Integer defnumber) {
                this.defnumber = defnumber;
        }
}
