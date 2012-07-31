package edgruberman.bukkit.playeractivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version implements Comparable<Version> {

    public final String original;
    public final Integer major;
    public final Integer minor;
    public final Integer revision;
    public final String type;
    public final Integer build;

    Version(final String version) {
        this.original = version;
        if (this.original == null) {
            this.major = null;
            this.minor = null;
            this.revision = null;
            this.type = null;
            this.build = null;
            return;
        }

        final String[] split = Pattern.compile("\\.|a|b|rc").split(this.original);
        this.major = Integer.parseInt(split[0]);
        this.minor = Integer.parseInt(split[1]);
        this.revision = Integer.parseInt(split[2]);
        final Matcher m = Pattern.compile("a|b|rc").matcher(this.original);
        this.type = (m.find() ? m.group(0) : null);
        this.build = (split.length >= 4 ? Integer.parseInt(split[3]) : null);
    }

    @Override
    public String toString() {
        return this.original;
    }

    @Override
    public int compareTo(final Version other) {
        // Null instances are less than any non-null instances
        if (other == null && this == null) return 0;
        if (other == null) return 1;
        if (this == null) return -1;

        // Null versions are less than any non-null version
        if (other.toString() == null && this.toString() == null) return 0;
        if (other.toString() == null) return 1;
        if (this.toString() == null) return -1;

        // Identical character matches are equivalent
        if (this.toString().equals(other.toString())) return 0;

        // Determine what is different, favoring the more important segments first
        if (this.major != other.major) return Integer.valueOf(this.major).compareTo(other.major);
        if (this.minor != other.minor) return Integer.valueOf(this.minor).compareTo(other.minor);
        if (this.revision != other.revision) return Integer.valueOf(this.revision).compareTo(other.revision);

        // A full release is more than a pre-release
        if (this.type == null) return 1;
        if (other.type == null) return -1;

        // Check the pre-release segments
        if (!this.type.equals(other.type)) return this.type.compareTo(other.type);
        return this.build.compareTo(other.build);
    }

}
