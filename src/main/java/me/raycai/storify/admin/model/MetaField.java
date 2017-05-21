package me.raycai.storify.admin.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MetaField {
    @Column
    private String key;
    @Column
    private String value;
    @Column
    private String valueType;
    @Column
    private String namespace;
    @Column
    private String description;

    public String getKey() {
        return key;
    }

    public MetaField setKey(String key) {
        this.key = key;
        return this;
    }

    public String getValue() {
        return value;
    }

    public MetaField setValue(String value) {
        this.value = value;
        return this;
    }

    public String getValueType() {
        return valueType;
    }

    public MetaField setValueType(String valueType) {
        this.valueType = valueType;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public MetaField setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MetaField setDescription(String description) {
        this.description = description;
        return this;
    }
}
