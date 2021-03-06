/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.requests;

import org.apache.kafka.common.protocol.ApiKeys;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.protocol.types.Struct;

import java.nio.ByteBuffer;

public class InitPidRequest extends AbstractRequest {
    public static final int NO_TRANSACTION_TIMEOUT_MS = Integer.MAX_VALUE;

    private static final String TRANSACTIONAL_ID_KEY_NAME = "transactional_id";
    private static final String TRANSACTION_TIMEOUT_KEY_NAME = "transaction_timeout_ms";

    private final String transactionalId;
    private final int transactionTimeoutMs;

    public static class Builder extends AbstractRequest.Builder<InitPidRequest> {
        private final String transactionalId;
        private final int transactionTimeoutMs;

        public Builder(String transactionalId) {
            this(transactionalId, NO_TRANSACTION_TIMEOUT_MS);
        }

        public Builder(String transactionalId, int transactionTimeoutMs) {
            super(ApiKeys.INIT_PRODUCER_ID);

            if (transactionTimeoutMs <= 0)
                throw new IllegalArgumentException("transaction timeout value is not positive: " + transactionTimeoutMs);

            if (transactionalId != null && transactionalId.isEmpty())
                throw new IllegalArgumentException("Must set either a null or a non-empty transactional id.");

            this.transactionalId = transactionalId;
            this.transactionTimeoutMs = transactionTimeoutMs;
        }

        @Override
        public InitPidRequest build(short version) {
            return new InitPidRequest(version, transactionalId, transactionTimeoutMs);
        }

        @Override
        public String toString() {
            return "(type=InitPidRequest, transactionalId=" + transactionalId + ", transactionTimeoutMs=" +
                    transactionTimeoutMs + ")";
        }
    }

    public InitPidRequest(Struct struct, short version) {
        super(version);
        this.transactionalId = struct.getString(TRANSACTIONAL_ID_KEY_NAME);
        this.transactionTimeoutMs = struct.getInt(TRANSACTION_TIMEOUT_KEY_NAME);
    }

    private InitPidRequest(short version, String transactionalId, int transactionTimeoutMs) {
        super(version);
        this.transactionalId = transactionalId;
        this.transactionTimeoutMs = transactionTimeoutMs;
    }

    @Override
    public AbstractResponse getErrorResponse(Throwable e) {
        return new InitPidResponse(Errors.forException(e));
    }

    public static InitPidRequest parse(ByteBuffer buffer, short version) {
        return new InitPidRequest(ApiKeys.INIT_PRODUCER_ID.parseRequest(version, buffer), version);
    }

    public String transactionalId() {
        return transactionalId;
    }

    public int transactionTimeoutMs() {
        return transactionTimeoutMs;
    }

    @Override
    protected Struct toStruct() {
        Struct struct = new Struct(ApiKeys.INIT_PRODUCER_ID.requestSchema(version()));
        struct.set(TRANSACTIONAL_ID_KEY_NAME, transactionalId);
        struct.set(TRANSACTION_TIMEOUT_KEY_NAME, transactionTimeoutMs);
        return struct;
    }

}
