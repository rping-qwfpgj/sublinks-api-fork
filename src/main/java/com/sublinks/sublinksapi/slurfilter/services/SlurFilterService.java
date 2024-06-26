package com.sublinks.sublinksapi.slurfilter.services;

import com.sublinks.sublinksapi.slurfilter.entities.SlurFilter;
import com.sublinks.sublinksapi.slurfilter.enums.SlurActionType;
import com.sublinks.sublinksapi.slurfilter.exceptions.SlurFilterBlockedException;
import com.sublinks.sublinksapi.slurfilter.exceptions.SlurFilterReportException;
import com.sublinks.sublinksapi.slurfilter.repositories.SlurFilterRepository;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SlurFilterService {

  private final SlurFilterRepository slurFilterRepository;

  @Transactional
  public void updateOrCreateLemmySlur(String regex) {

    SlurFilter slurFilter = slurFilterRepository.findAll(Sort.by(Direction.ASC, "id"))
        .stream()
        .findFirst()
        .orElse(null);
    if (slurFilter == null) {
      slurFilter = SlurFilter.builder()
          .slurRegex(regex)
          .slurActionType(SlurActionType.BLOCK)
          .build();
    } else {
      slurFilter.setSlurRegex(regex);
    }
    slurFilterRepository.save(slurFilter);
  }

  public SlurFilter getLemmySlurFilter() {

    SlurFilter slurFilter = slurFilterRepository.findAll(Sort.by(Direction.ASC, "id"))
        .stream()
        .findFirst()
        .orElse(null);
    if (slurFilter == null) {
      slurFilter = SlurFilter.builder().slurRegex("").slurActionType(SlurActionType.BLOCK).build();
      slurFilterRepository.save(slurFilter);
    }
    return slurFilter;
  }

  public SlurFilter getHighestSlurFilterMatchingText(String text) {

    List<SlurFilter> slurFilters = slurFilterRepository.findAll();
    if (text == null || text.isEmpty() || text.isBlank() || slurFilters.isEmpty()) {
      return null;
    }

    return slurFilters.stream().filter(
        slurFilter -> !slurFilter.getSlurRegex().isBlank() && Pattern.compile(
            slurFilter.getSlurRegex()).matcher(text).find()).max(
        Comparator.comparing(SlurFilter::getSlurActionType)).orElse(null);
  }

  public String censorText(final String text)
      throws SlurFilterBlockedException, SlurFilterReportException {

    String censoredText = text;
    SlurFilter filter = getHighestSlurFilterMatchingText(text);
    if (filter == null) {
      return censoredText;
    }

    if (filter.getSlurActionType() == SlurActionType.BLOCK) {
      throw new SlurFilterBlockedException("Text blocked by slur filter", filter);
    } else if (filter.getSlurActionType() == SlurActionType.REPORT) {
      throw new SlurFilterReportException("Text should be reported", filter);
    }

    List<SlurFilter> slurFilters = slurFilterRepository.findBySlurActionType(
        SlurActionType.REPLACE);
    if (censoredText == null || censoredText.isEmpty() || censoredText.isBlank()
        || slurFilters.isEmpty()) {
      return censoredText;
    }

    List<Pattern> patterns = slurFilters.stream().map(
        (pattern) -> Pattern.compile(pattern.getSlurRegex(), Pattern.CASE_INSENSITIVE)).toList();

    for (Pattern pattern : patterns) {
      // Censor all found text with the same word count of the found word with *
      censoredText = pattern.matcher(censoredText).replaceAll(
          match -> "*".repeat(match.group().length()));
    }
    return censoredText;
  }
}
