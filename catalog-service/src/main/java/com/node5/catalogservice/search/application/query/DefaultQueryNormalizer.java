package com.node5.catalogservice.search.application.query;

import java.text.Normalizer;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultQueryNormalizer implements QueryNormalizer {

	private static final int MAX_LENGTH = 64;

	@Override
	public String normalize(String raw) {
		if (!StringUtils.hasText(raw)) {
			return "";
		}

		String s = raw.trim();

		s = Normalizer.normalize(s, Normalizer.Form.NFKC);

		s = s.replaceAll("[-_/]+", " ");

		s = s.replaceAll("\\s+", " ").trim();

		s = toLowerAscii(s);

		if (s.length() > MAX_LENGTH) {
			s = s.substring(0, MAX_LENGTH).trim();
		}

		s = s.replaceAll("\\s+", " ").trim();

		return s;
	}

	private String toLowerAscii(String input) {
		StringBuilder sb = new StringBuilder(input.length());
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c >= 'A' && c <= 'Z') {
				sb.append((char) (c + 32));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
