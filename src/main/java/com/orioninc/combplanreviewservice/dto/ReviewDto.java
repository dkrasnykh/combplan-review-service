package com.orioninc.combplanreviewservice.dto;

import com.orioninc.combplanreviewservice.streamconsumer.UserStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Random;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReviewDto extends AbstractDto {
    private UserDto reviewer;
    private RequestDto request;

    public ReviewDto() {
    }

    public ReviewDto(Builder builder) {
        reviewer = builder.reviewer;
        request = builder.request;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder builder(RequestDto request) {
        return new Builder(request);
    }

    public static final class Builder {
        private UserDto reviewer;
        private RequestDto request;

        private Builder() {
        }

        private Builder(RequestDto request) {

            UserDto user = null;
            Random random= new Random();
            if (UserStore.users.size() > 0) {
                user = UserStore.users.get(random.nextInt(UserStore.users.size()));
            }

            this.reviewer = user;
            this.request = request;
        }

        public Builder reviewer(UserDto val) {
            reviewer = val;
            return this;
        }

        public Builder request(RequestDto val) {
            request = val;
            return this;
        }

        public ReviewDto build() {
            return new ReviewDto(this);
        }
    }
}
