package com.telenav.fiasco.plugins;

import com.telenav.kivakit.filesystem.File;
import com.telenav.kivakit.interfaces.comparison.Matcher;

import java.util.regex.Pattern;

/**
 * Matches resources using a simplified pattern matching syntax.
 * <p>
 * <b>Matching Syntax</b>
 * <ul>
 *     <li>* - matches zero or more filename characters</li>
 *     <li>** - matches zero or more folders</li>
 * </ul>
 *
 * @author jonathanl (shibo)
 */
public class FilePattern implements Matcher<File>
{
    public static FilePattern parse(String simplifiedPattern)
    {
        final var files = "\0";
        final var characters = "\1";

        // Substitute \0 for /**/ and \1 for *,
        simplifiedPattern = simplifiedPattern
                .replaceAll("[^/]\\*\\*[$/]", files)
                .replaceAll("\\*", characters);

        // quote characters around \0 and \1,
        var matcher = Pattern.compile("(.*)([\0\1])").matcher(simplifiedPattern);
        simplifiedPattern = matcher.replaceAll("\\Q$1\\E$2");
        matcher = Pattern.compile("([\0\1])(.*)$").matcher(simplifiedPattern);
        simplifiedPattern = matcher.replaceAll("\\Q$1\\E$2");

        // then build the final pattern by substituting regexps for \0 and \1
        final var pattern = simplifiedPattern
                .replaceAll(files, ".*")
                .replaceAll(characters, "[^/]*");

        return new FilePattern(Pattern.compile(pattern));
    }

    private final Pattern pattern;

    private FilePattern(final Pattern pattern)
    {
        this.pattern = pattern;
    }

    @Override
    public boolean matches(final File file)
    {
        final var path = file.path().join("/");
        return pattern.matcher(path).matches();
    }
}
