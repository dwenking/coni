package coni.connector.result;

import java.util.Objects;

public class MessageResult extends Result{
    private String message;

    @Override
    public void print() {
        System.out.println(this.owner + " message: " + this.message);
    }

    @Override
    public void printInfo() {
        logger.info(this.owner + " message: " + this.message);
    }

    @Override
    public void printError() {
        logger.error(this.owner + " message: " + this.message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MessageResult result = (MessageResult) o;

        return this.message == result.message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.owner, this.message);
    }

    public MessageResult(String owner, String message) {
        super(owner, null);
        this.message = message;
    }
}
