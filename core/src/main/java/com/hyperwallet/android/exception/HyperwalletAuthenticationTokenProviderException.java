/*
 * Copyright 2018 Hyperwallet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package com.hyperwallet.android.exception;

import androidx.annotation.RestrictTo;

import java.util.UUID;

/**
 * An instance of {@code HyperwalletAuthenticationTokenProviderException} is created to comply with the
 * {@link com.hyperwallet.android.ExceptionMapper} interface when an authentication token retrieval fails and the
 * {@link com.hyperwallet.android.HyperwalletAuthenticationTokenListener#onFailure(UUID, String)} is called.
 *
 * <p>The message parameter from the {@code onFailure} call is transferred to the
 * {@code HyperwalletAuthenticationTokenProviderException} for use upstream.</p>
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class HyperwalletAuthenticationTokenProviderException extends Exception {

    /**
     * Create a {@code HyperwalletAuthenticationTokenProviderException} with a message describing the reason for the
     * failure.
     *
     * @param message {@code String} describing the reason for the failure
     */
    public HyperwalletAuthenticationTokenProviderException(String message) {
        super(message);
    }

}
