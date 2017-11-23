package gob.bcb.lavado.client.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class UtilsGeneric {
	private static final String MAIL_REGEX2 = "^(([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5}){1,25})+([;.](([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5}){1,25})+)*$";
	public final static String LINE_SEPARATOR;

	static {
		LINE_SEPARATOR = System.getProperty("line.separator");
	}

	/**
	 * Generate a valid xs:ID string.
	 */

	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public static Object newInstance(String name) {
		try {
			Class<?> c = Class.forName(name);
			return c.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("No se puede crear instancia de " + name, e);
		}
	}

	public static void validarEmail(String emailAddress) {
		if (StringUtils.isBlank(emailAddress)) {
			return;
		}
		Pattern mask = null;

		mask = Pattern.compile(MAIL_REGEX2);
		Matcher matcher = mask.matcher(emailAddress);

		if (!matcher.matches()) {
			throw new RuntimeException("Direccion de correo invalido " + emailAddress);
		}

	}

	public static boolean validarEmail2(String emailAddress) {
		try {
			validarEmail(emailAddress);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String truncate(String string0, int maxLength, String suffix, boolean defaultTruncateAtWord) {
		if (StringUtils.isBlank(string0)) {
			return null;
		}
		String string = StringUtils.trimToEmpty(string0);
		if (string.length() <= maxLength) {
			return string + suffix;
		}
		if (suffix == null || maxLength - suffix.length() <= 0) {
			return StringUtils.trimToEmpty(string.substring(0, maxLength));
		}

		if (defaultTruncateAtWord) {
			// find the latest space within maxLength
			String sc = string.substring(0, maxLength + 1);
			int lastSpace = sc.lastIndexOf(" ");
			// int lastSpace = string.substring(0, maxLength - suffix.length() +
			// 1).lastIndexOf(" ");
			if (lastSpace > 0) {
				return StringUtils.trimToEmpty(string.substring(0, lastSpace)) + suffix;
			}
		}
		// truncate to exact character and append suffix
		return StringUtils.trimToEmpty(string.substring(0, maxLength)) + suffix;

	}

	public static String truncate(String string0, int maxLength, String suffix, String tok, boolean defaultTruncateAtWord) {
		if (StringUtils.isBlank(string0)) {
			return null;
		}
		String string = StringUtils.trimToEmpty(string0);
		if (string.length() <= maxLength) {
			return string;
		}

		if (suffix == null || maxLength - suffix.length() <= 0) {
			// either no need or no room for suffix
			return StringUtils.trimToEmpty(string.substring(0, maxLength));
		}
		if (defaultTruncateAtWord) {
			// find the latest space within maxLength
			String s = string.substring(0, maxLength);
			String s1 = string.substring(0, maxLength - suffix.length());
			System.out.println(s + "[" + s.length() + "]" + " && " + s1 + "[" + s1.length() + "]");

			int lastSpace = s.lastIndexOf(suffix);
			if (lastSpace >= 0) {
				return StringUtils.trimToEmpty(string.substring(0, lastSpace + 1));
			}
		}
		return StringUtils.trimToEmpty(string.substring(0, maxLength));

	}

	/**
	 * divide una cadena en lineas de tamaño fijo o menor al prefijo
	 * 
	 * @param string0
	 *            cadena a dividir
	 * @param maxLength
	 *            maximo de una linea
	 * @param suffix
	 *            token a buscar
	 * @param tok
	 * @param defaultTruncateAtWord
	 *            true si se trunca antes del sufijo false si es de tamaño fijo
	 * @return
	 */
	public static List<String> splitInLines(String string0, int maxLength, String suffix, String tok, boolean defaultTruncateAtWord) {
		String frase = "";
		List<String> result = new ArrayList<String>();
		do {
			string0 = StringUtils.trimToEmpty(string0);
			string0 = string0.substring(frase.length());
			frase = UtilsGeneric.truncate(string0, maxLength, suffix, tok, defaultTruncateAtWord);
			frase = StringUtils.trimToEmpty(frase);
			if (StringUtils.isNotBlank(frase)) {
				result.add(frase);
			}
		} while (StringUtils.isNotBlank(frase));
		return result;
	}

	public static String newStringFromBytes(byte[] bytes, String charsetName) {
		try {
			return new String(bytes, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Impossible failure: Charset.forName(\"" + charsetName + "\") returns invalid name.");

		}
	}

	public static final Charset ISO_CHARSET = Charset.forName("UTF-8");

	public static String newStringFromString(String cadena) {
		if (cadena == null)
			return null;
		return newStringFromBytes(cadena.getBytes(), ISO_CHARSET.name());
	}

	public static Map<String, String> paramsLista(String text) {
		// byte[] valueDecoded= Base64.decode(bytesEncoded);
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (text == null || text.trim().length() == 0) {
			return map;
		}
		String params = new String(text);

		for (String keyValue : params.split(" *& *")) {
			String[] pairs = keyValue.split(" *= *", 2);
			map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
		}
		return map;
	}

	public static Map<String, String> paramsLista(String text, String p1, String p2) {
		// byte[] valueDecoded= Base64.decode(bytesEncoded);
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (text == null || text.trim().length() == 0) {
			return map;
		}
		String params = new String(text);

		for (String keyValue : params.split(" *" + p1 + " *")) {
			String[] pairs = keyValue.split(" *" + p2 + " *", 2);
			map.put(pairs[0], pairs.length == 1 ? "" : pairs[1]);
		}
		return map;
	}

	public static BigDecimal bigDecimalFromString(String valor) {
		if (StringUtils.isBlank(valor)) {
			return null;
		}
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		String pattern = "#,##0.0#";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		decimalFormat.setParseBigDecimal(true);

		// parse the string
		BigDecimal bigDecimal;
		try {
			bigDecimal = (BigDecimal) decimalFormat.parse(valor);
		} catch (ParseException e) {
			throw new RuntimeException("Error al convertir '" + valor + "' a BigDecimal" + e.getMessage());
		}
		return bigDecimal;
	}

	public static String formatearMonto(BigDecimal monto) {
		return formatearMonto(monto, true);
	}

	public static String formatearMonto(BigDecimal monto, boolean conSeparacion) {
		String montoLiteral0 = String.format("%.2f", monto);
		String pd = ".";
		String psm = ",";
		if (montoLiteral0.indexOf(",") > 0) {
			pd = ",";
			psm = ".";
		}
		String montoLiteral = "";
		if (conSeparacion) {
			montoLiteral = String.format("%,.2f", monto);
		} else {
			montoLiteral = String.format("%.2f", monto);
		}

		String mm = montoLiteral.replace(pd.charAt(0), '$');
		String mm0 = mm.replace(psm.charAt(0), '#');

		mm = mm0.replace('$', ',');
		mm0 = mm.replace('#', '.');

		String montoT = mm0;

		return montoT;

	}

	public static Map<String, String> valoresVar(String cadenaVars) {
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isBlank(cadenaVars)) {
			return map;
		}
		StringTokenizer tok = new StringTokenizer(cadenaVars, "@", true);
		while (tok.hasMoreTokens()) {
			String entry = tok.nextToken();
			if (entry.length() == 1) {
				entry = tok.nextToken(" ").trim();
				map.put("@" + entry, "");
				try {
					tok.nextToken("@");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return map;
	}

	public static List<String> parse(String regex, String string) {
		Pattern pat = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pat.matcher(string);
		List<String> result = new ArrayList<String>();
		if (matcher.matches()) {
			int groups = matcher.groupCount();
			for (int i = 1; i <= groups; i++) {
				result.add(matcher.group(i));
			}
		} else {
			System.err.println("Not matched");
		}
		return result;
	}

	public static String getShortText(String string, int len) {
		return (string == null ? "" : (string.length() <= len) ? string : (string.substring(0, len - 3) + "..."));
	}

	public static String encodeURL(String url, String sessionParam, String sessionAlias) {
		String encodedSessionAlias = urlEncode(sessionAlias);
		int queryStart = url.indexOf("?");

		if (queryStart < 0) {
			return url + "?" + sessionParam + "=" + encodedSessionAlias;
		}

		String path = url.substring(0, queryStart);
		String query = url.substring(queryStart + 1, url.length());
		String replacement = "$1" + encodedSessionAlias;
		query = query.replaceFirst("((^|&)" + sessionParam + "=)([^&]+)?", replacement);
		if (url.endsWith(query)) {
			// no existing alias
			if (!url.contains(sessionParam)) {
				if (!(query.endsWith("&") || query.length() == 0)) {
					query += "&";
				}
				query += sessionParam + "=" + encodedSessionAlias;
			}
		}

		return path + "?" + query;
	}

	public static String urlEncode(String value) {
		try {
			if (value == null)
				value = "";
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Convierte una cadena con un encoding determinado util para base de datos
	 * 
	 * @param value
	 *            cadena a convertir
	 * @param charsetName
	 *            CHARSET ISO-8859-1 , UTF-8
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String convertStringToEncode(String value, String charsetName) throws UnsupportedEncodingException {
		if (value == null)
			return null;
		String encoded = URLEncoder.encode(value, charsetName);
		String decoded = URLDecoder.decode(encoded, charsetName);
		return decoded;
	}

	public static String encodeQueryParams(Map<String, String> parameters) throws UnsupportedEncodingException {
		String result = "";
		if (parameters != null) {
			for (String paramName : parameters.keySet()) {
				result += URLEncoder.encode(paramName, "UTF-8");
				result += "=" + URLEncoder.encode(parameters.get(paramName), "UTF-8") + "&";
			}
		}
		
		if (result.endsWith("&")) {
			result = result.substring(0, result.lastIndexOf("&"));
		}
		return result;
	}
	
	public static void main(String[] args) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("codapp", "codapp");
		params.put("coduser", "coduser");
		params.put("coduser", "");		
		try {
			System.out.println(encodeQueryParams(params));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}		
	}
}
