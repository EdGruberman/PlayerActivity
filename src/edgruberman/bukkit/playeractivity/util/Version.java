package edgruberman.bukkit.playeractivity.util;


import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Format: Major.Minor.Revision[(Type)Build] Example: 1.2.3a17 */
public final class Version implements Comparable<Version> {

    public final String original;
    public final Integer major;
    public final Integer minor;
    public final Integer revision;
    public final Type type;
    public final Integer build;

    public Version(final String version) {
        this.original = version;

        if (version == null) {
            this.major = null;
            this.minor = null;
            this.revision = null;
            this.type = null;
            this.build = null;
            return;
        }

        final Matcher m = Pattern.compile("(\\d+)\\.(\\d+).(\\d+)(a|b|rc|$)(\\d+)?").matcher(this.original);
        if (!m.find())
            throw new IllegalArgumentException("Unrecognized version format \"" + version + "\"; Expected <Major>.<Minor>.<Revision>[<Type><Build>]");

        this.major = Integer.parseInt(m.group(1));
        this.minor = Integer.parseInt(m.group(2));
        this.revision = Integer.parseInt(m.group(3));
        this.type = Type.parse(m.group(4));
        this.build = (m.group(5) != null ? Integer.parseInt(m.group(5)) : null);
    }

    @Override
    public String toString() {
        return this.original;
    }

    @Override
    public int compareTo(final Version other) {
        // Null instances are less than any non-null instances
        if (other == null) return 1;
        if (this.original != null && other.original == null) return 1;
        if (this.original == null && other.original == null) return 0;
        if (this.original == null && other.original != null) return -1;

        if (this.original.equals(other.original)) return 0;

        // Determine what is different, favoring the more important segments first
        if (this.major != other.major) return this.major.compareTo(other.major);
        if (this.minor != other.minor) return this.minor.compareTo(other.minor);
        if (this.revision != other.revision) return this.revision.compareTo(other.revision);
        if (this.type != other.type) return this.type.compareTo(other.type);
        return (this.build == null ? -1 : this.build.compareTo(other.build));
    }


    /** Software life-cycle indicator (Alpha -> Beta -> Release Candidate -> Production) */
    public static class Type implements Comparable<Type> {

        private static final Collection<Type> known = new ArrayList<Type>();

        public static final Type ALPHA = new Type("a", 0);
        public static final Type BETA = new Type("b", 1);
        public static final Type CANDIDATE = new Type("rc", 2);
        public static final Type PRODUCTION = new Type("", 3);

        public static Type parse(final String designator) {
            for (final Type type : Type.known)
                if (type.designator.equals(designator))
                    return type;

            throw new IllegalArgumentException("Unknown designator: " + designator);
        }

        public final String designator;
        public final Integer level;

        private Type(final String designator, final int level) {
            this.designator = designator;
            this.level = level;
            Type.known.add(this);
        }

        @Override
        public int compareTo(final Type other) {
            return (other == null ? 1 : this.level.compareTo(other.level));

        }

    }

}
