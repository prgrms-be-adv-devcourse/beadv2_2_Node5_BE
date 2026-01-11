package com.node5.catalogservice.search.application.query;

import java.text.Normalizer;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DefaultQueryNormalizer implements QueryNormalizer {

	private static final int MAX_LENGTH = 64;

	@Override
	public String normalize(String raw) {
		if (!StringUtils.hasText(raw)) return "";

		String s = raw.trim();
		s = Normalizer.normalize(s, Normalizer.Form.NFKC);

		// 구분자 통일: -, _, / → 공백
		s = s.replaceAll("[-_/]+", " ");

		// 공백 정리 + 영문 소문자
		s = toLowerAscii(s);
		s = s.replaceAll("\\s+", " ").trim();

		if (s.length() > MAX_LENGTH) {
			s = s.substring(0, MAX_LENGTH).trim();
		}

		// substring 이후 공백이 생길 수 있어서 1번 더 공백 정리
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
