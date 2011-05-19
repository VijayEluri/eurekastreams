/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.server.service.actions.strategies;

import java.io.Serializable;
import java.util.Map;

import org.eurekastreams.commons.exceptions.ValidationException;

/**
 * Rejects with validation error if key has configured value. Good for rejecting default values if user must select a
 * change.
 */
public class MapParameterRejectStringValueValidator implements MapParameterValidatorDecorator
{

    /**
     * Local instance of the validatorDecorator to be called.
     */
    private MapParameterValidatorDecorator valDecorator;

    /**
     * Local instance of the key to the map to test the length of the value.
     */
    private String key;

    /**
     * String value to reject.
     */
    private String valueToReject;

    /**
     * Local instance of the message to return to the caller when validation fails.
     */
    private String message;

    /**
     * Constructor.
     * 
     * @param inKey
     *            - key in the map to validate the string value for.
     * @param inValueToReject
     *            - String value to reject.
     * @param inMessage
     *            - message when validation fails.
     */
    public MapParameterRejectStringValueValidator(final String inKey, final String inValueToReject,
            final String inMessage)
    {
        key = inKey;
        valueToReject = inValueToReject;
        message = inMessage;
    }

    /**
     * {@inheritDoc}
     */
    public void setMapParameterValidatorDecorator(final MapParameterValidatorDecorator inDecorated)
    {
        valDecorator = inDecorated;
    }

    /**
     * Puts error on errors map if the provided key matches a key in the map, the value for that key is a string and the
     * string is match for supplied value.
     * 
     * @param map
     *            - map of values to validate.
     * @param errors
     *            - map of errors.
     */
    public void validate(final Map<String, Serializable> map, final Map<String, String> errors)
    {
        if (map.get(key) != null && (map.get(key) instanceof String)
                && (((String) map.get(key)).compareToIgnoreCase(valueToReject)) == 0)
        {
            errors.put(key, message);
        }

        if (valDecorator != null)
        {
            valDecorator.validate(map, errors);
        }
        else if (!errors.isEmpty())
        {
            ValidationException ve = new ValidationException();
            for (String errorKey : errors.keySet())
            {
                ve.addError(errorKey, errors.get(errorKey));
            }
            throw ve;
        }
    }
}
