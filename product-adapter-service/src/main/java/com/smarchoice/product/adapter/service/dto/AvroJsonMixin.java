package com.smarchoice.product.adapter.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificData;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AvroJsonMixin
{
    /**
     * Ignore the Avro schema property.
     */
    @JsonIgnore
    abstract Schema getSchema();
    /**
     * Ignore the specific data property.
     */
    @JsonIgnore
    abstract SpecificData getSpecificData();
}