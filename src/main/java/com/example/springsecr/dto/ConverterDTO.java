package com.example.springsecr.dto;

public interface ConverterDTO <DTO,ENTITY>
{
    public DTO convertToDTO(ENTITY object);
    public ENTITY convertedToEntity(DTO object);
}
