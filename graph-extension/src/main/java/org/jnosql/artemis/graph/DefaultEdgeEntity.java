/*
 *  Copyright (c) 2017 Otávio Santana and others
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package org.jnosql.artemis.graph;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.jnosql.diana.api.Value;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

class DefaultEdgeEntity<IN, OUT> implements EdgeEntity<IN, OUT> {

    private final OUT outbound;

    private final Edge edge;

    private final IN inbound;

    DefaultEdgeEntity(Edge edge, IN inbound, OUT outbound) {
        this.edge = edge;
        this.inbound = inbound;
        this.outbound = outbound;
    }

    @Override
    public Value getId() {
        return Value.of(edge.id());
    }

    @Override
    public String getLabel() {
        return edge.label();
    }

    @Override
    public IN getInbound() {
        return inbound;
    }

    @Override
    public OUT getOutbound() {
        return outbound;
    }

    @Override
    public List<ArtemisElement> getProperties() {
        return edge.keys()
                .stream()
                .map(k -> ArtemisElement.of(k, Value.of(edge.value(k))))
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    @Override
    public void add(String key, Object value) throws NullPointerException {
        requireNonNull(key, "key is required");
        requireNonNull(value, "value is required");
        edge.property(key, value);

    }

    @Override
    public void add(String key, Value value) throws NullPointerException {
        requireNonNull(key, "key is required");
        requireNonNull(value, "value is required");
        edge.property(key, value.get());
    }

    @Override
    public void remove(String key) throws NullPointerException {
        requireNonNull(key, "key is required");
        Property<Object> property = edge.property(key);
        property.ifPresent(o -> property.remove());
    }

    @Override
    public Optional<Value> get(String key) throws NullPointerException {
        requireNonNull(key, "key is required");
        Property<Object> property = edge.property(key);
        if (property.isPresent()) {
            return Optional.of(Value.of(property.value()));
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultEdgeEntity)) {
            return false;
        }
        DefaultEdgeEntity<?, ?> that = (DefaultEdgeEntity<?, ?>) o;
        return Objects.equals(edge, that.edge) &&
                Objects.equals(inbound, that.inbound) &&
                Objects.equals(outbound, that.outbound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge, inbound, outbound);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefaultEdgeEntity{");
        sb.append("outbound=").append(outbound);
        sb.append(", edge=").append(edge);
        sb.append(", inbound=").append(inbound);
        sb.append('}');
        return sb.toString();
    }
}