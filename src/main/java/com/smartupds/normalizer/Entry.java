package com.smartupds.normalizer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Yannis Marketakis
 */
@Data @AllArgsConstructor
public class Entry {
    private String image1;
    private String image2;
    private String measurement;
    protected static String entryName;
    protected static String measurementName;
    
    @Override
    public boolean equals(Object anotherObject){
        if(anotherObject instanceof Entry){
            Entry anotherEntry=(Entry)anotherObject;
            if(this.image1.equals(anotherEntry.image2) && this.image2.equals(anotherEntry.image1)){
                return true;
            }else if(this.image1.equals(anotherEntry.image1) && this.image2.equals(anotherEntry.image2)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    
    @Override
    public int hashCode(){
        return this.image1.hashCode()+this.image2.hashCode();
    }
    
    public String toXML(){
        return "<"+Entry.entryName+">\n"+
                "<image1>"+this.image1+"</image1>\n"+
                "<image2>"+this.image2+"</image2>\n"+
                "<"+Entry.measurementName+">"+this.measurement+"</"+Entry.measurementName+">\n"+
                "</"+Entry.entryName+">";
                
    }
}