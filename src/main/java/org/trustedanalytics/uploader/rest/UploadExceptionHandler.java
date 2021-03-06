/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.uploader.rest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.trustedanalytics.utils.errorhandling.ErrorLogger;
import org.trustedanalytics.utils.errorhandling.RestErrorHandler;

import org.apache.hadoop.security.AccessControlException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class UploadExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadExceptionHandler.class);

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, TypeMismatchException.class})
    public void invalidRequest(Exception ex, HttpServletResponse response)  throws IOException {
        ErrorLogger.logAndSendErrorResponse(LOGGER, response, BAD_REQUEST, "Invalid request", ex);
    }

    @ExceptionHandler({FileUploadException.class, IOException.class})
    public void fileUploadException(Exception ex, HttpServletResponse response)  throws IOException {
        ErrorLogger.logAndSendErrorResponse(LOGGER, response, INTERNAL_SERVER_ERROR, "File upload exception", ex);
    }

    @ExceptionHandler({AccessDeniedException.class, AccessControlException.class})
    public void accessForbidden(Exception ex, HttpServletResponse response) throws IOException {
        ErrorLogger.logAndSendErrorResponse(LOGGER, response, FORBIDDEN, "You do not have access to requested organization", ex);
    }

    @ExceptionHandler(FeignResponseException.class)
    public void handleFeignException(FeignResponseException e, HttpServletResponse response) throws IOException {
        ErrorLogger.logAndSendErrorResponse(LOGGER, response, e.getStatusCode(), e.getMessage(), e);
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletResponse response) throws Exception {
        RestErrorHandler defaultErrorHandler = new RestErrorHandler();
        defaultErrorHandler.handleException(e, response);
    }

    @ExceptionHandler(UploadException.class)
    public void handleUploadException(UploadException e, HttpServletResponse response) throws IOException {
        ErrorLogger.logAndSendErrorResponse(LOGGER, response, INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }
}
