package dk.tinker.authservice.domain;

public enum PermissionLevel {
    READ, WRITE, ADMIN;

    public boolean includes(PermissionLevel required) {
        return this.ordinal() >= required.ordinal();
    }
}
