package com.eu.at_it.pantheon.validation;

import com.eu.at_it.pantheon.helper.EndpointInput;

/**
 * for future use in validation, for example to confirm old password before updating to new
 */
public interface EndpointValidator extends Validator<EndpointInput> {
    @Override
    boolean test(EndpointInput endpointInput);
}
