package com.smartupds.normalizer.exceptions;

/** Generic Exceptions class
 * 
 * @author Yannis Marketakis (SmartUp Data Solutions)
 */
public class NormalizerException extends Exception{
    
    public NormalizerException(String message){
        super(message);
    }
    
    public NormalizerException(String message, Throwable thr){
        super(message, thr);
    }
}