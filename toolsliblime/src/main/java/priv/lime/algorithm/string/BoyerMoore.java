package priv.lime.algorithm.string;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 传说中的BM(BoyerMoore)算法 2016.07.15
 * 
 * @author Lime(李振宇)
 * @version 0.1
 * @since 2016.07.15
 * <p>简介： 在计算机科学里，Boyer-Moore字符串搜索算法是一种非常高效的字符串搜索算法。 它由Bob Boyer和J
 *          Strother Moore设计于1977年。此算法仅对搜索目标字符串（关键字）进行预处理，
 *          而非被搜索的字符串。虽然Boyer-Moore算法的执行时间同样线性依赖于被搜索字符串的大小，
 *          但是通常仅为其它算法的一小部分：它不需要对被搜索的字符串中的字符进行逐一比较，
 *          而会跳过其中某些部分。通常搜索关键字越长，算法速度越快。它的效率来自于这样的事实：
 *          对于每一次失败的匹配尝试，算法都能够使用这些信息来排除尽可能多的无法匹配的位置。
 * 
 *          原理： 不同于朴素模式（brute-force
 *          search）的逐个字符对比，Boyer-Moore充分使用预处理P的信息来尽可能跳过更多的字符。
 *          通常，我们比较一个字符串都是从首字母开始，逐个比较下去。一旦发现有不同的字符，就需要从头开始进行下一次比较。
 *          这样，就需要将字串中的所有字符一一比较。Boyer-Moore算法的关键在于，当P的最后一个字符被比较完成后，
 *          我们可以决定跳过一个或更多个字符。如果最后一个字符不匹配，那么就没必要继续比较前一个字符。
 *          如果最后一个字符未在P中出现，那么我们可以直接跳过T的n个字符，比较接下来的n个字符，n为P的长度（见定义）。
 *          如果最后一个字符出现在P中，那么跳过的字符数需要进行计算（也就是将P整体往后移），然后继续前面的步骤来比较。
 *          通过这种字符的移动方式来代替逐个比较是这个算法如此高效的关键所在。
 *          形式化的表述方式为，从k=n开始，也就是P从T的最开始进行比较。紧接着，P的第n个字符和T的第k个字符开始：
 *          字符串依次从P的最后一个字符到最开始的字符。结束条件是当比较到达P的最开始（此时匹配完成），或按照规则移动后的字符部匹配发生时。
 *          然后，在新的对齐位置重新开始比较，如此反复，直到到达T的结尾。移动规则是一张间恒定的查找表，通过对P的预处理产生的。
 * </p>
 * <p>
 *          借鉴：http://www-igm.univ-mlv.fr/~lecroq/string/node14.html
 *          算法详解：(阮一峰博客)http://www.ruanyifeng.com/blog/2013/05/boyer-moore_string_search_algorithm.html
 *          使用方法：
 * </p>
 *          <pre>
 * <code>
 * 	BoyerMoore bm = new BoyerMoore();
 *	//查找一次，返回地址
 *	int index = boyerMoore.matchOne("abc", "aaaabc");
 * </code>
 *          </pre>
 */
public class BoyerMoore {

	/**
	 * 坏字符规则
	 */
	private Map<Character, Integer> badChar;

	/**
	 * 好后缀规则数组
	 */
	private int[] goodSuffix;

	/**
	 * 初始化
	 */
	public BoyerMoore() {
		super();
		badChar = new HashMap<Character, Integer>();
	}

	/**
	 * 创建坏字符规则
	 * 
	 * @param pattern
	 *            查询(模块字符串)关键词
	 */
	private void preBadChar(String pattern) {
		/*清除上一次使用数据*/
		badChar.clear();
		for (int i = 0; i < pattern.length(); ++i) {
			if (!badChar.containsKey(pattern.charAt(i))) {
				badChar.put(pattern.charAt(i), pattern.length() - i - 1);
			}
		}
	}

	/**
	 * 构建后缀数组
	 * 
	 * @param pattern
	 *            查询(模块字符串)关键词
	 * @param suff
	 *            后缀数组
	 */
	private void suffixes(String pattern, int[] suff) {
		int f = 0, g, i, m = pattern.length();
		suff[m - 1] = m;
		g = m - 1;
		for (i = m - 2; i >= 0; --i) {
			if (i > g && suff[i + m - 1 - f] < i - g)
				suff[i] = suff[i + m - 1 - f];
			else {
				if (i < g)
					g = i;
				f = i;
				while (g >= 0 && pattern.charAt(g) == pattern.charAt(g + m - 1 - f))
					--g;
				suff[i] = f - g;
			}
		}
	}

	/**
	 * 创建好后缀规则
	 * 
	 * @param pattern
	 *            查询(模块字符串)关键词
	 */
	private void preGoodSuff(String pattern) {
		int i, j, m = pattern.length();
		int[] suff = new int[pattern.length()];
		goodSuffix = new int[pattern.length()];

		suffixes(pattern, suff);
		for (i = 0; i < m; ++i)
			goodSuffix[i] = m;
		j = 0;
		for (i = m - 1; i >= 0; --i)
			if (suff[i] == i + 1)
				for (; j < m - 1 - i; ++j)
					if (goodSuffix[j] == m)
						goodSuffix[j] = m - 1 - i;
		for (i = 0; i <= m - 2; ++i)
			goodSuffix[m - 1 - suff[i]] = m - 1 - i;
	}

	/**
	 * 仅查询一次并返回索引值
	 * 
	 * @param pattern
	 *            查询(模块字符串)关键词
	 * @param text
	 *            全文，被查询文本
	 * @return 返回匹配索引
	 */
	public int matchOne(String pattern, String text) {
		int i, j;
		/* Preprocessing */
		preBadChar(pattern);
		preGoodSuff(pattern);

		/* Searching */
		j = 0;
		while (j <= text.length() - pattern.length()) {
			for (i = pattern.length() - 1; i >= 0 && pattern.charAt(i) == text.charAt(i + j); --i)
				;
			if (i < 0)
				return j;
			else {
				/* 是否存在坏字符规则中 */
				int bad;
				if (badChar.containsKey(text.charAt(i + j)))
					bad = badChar.get(text.charAt(i + j)) - pattern.length() + 1 + i;
				else
					bad = 1 + i;
				j += max(goodSuffix[i], bad);
			}
		}
		return -1;
	}

	/**
	 * 查找所有匹配字符串
	 * 
	 * @param pattern
	 *            查询(模块字符串)关键词
	 * @param text
	 *            全文，被查询文本
	 * @return 返回匹配查询结果索引集合
	 */
	public List<Integer> matchAll(String pattern, String text) {
		int i, j;
		List<Integer> list = new ArrayList<Integer>();
		/* Preprocessing */
		preBadChar(pattern);
		preGoodSuff(pattern);
		/* Searching */
		j = 0;
		while (j <= text.length() - pattern.length()) {
			for (i = pattern.length() - 1; i >= 0 && pattern.charAt(i) == text.charAt(i + j); --i)
				;
			if (i < 0) {
				list.add(j);
				j += goodSuffix[0];
			} else {
				/* 是否存在坏字符规则中 */
				int bad;
				if (badChar.containsKey(text.charAt(i + j)))
					bad = badChar.get(text.charAt(i + j)) - pattern.length() + 1 + i;
				else
					bad = 1 + i;
				j += max(goodSuffix[i], bad);
			}
		}
		return list;
	}

	/**
	 * 仅查询一次并返回索引值
	 * 
	 * @param pattern
	 *            查询(模块字符串)关键词
	 * @param text
	 *            全文，被查询文本
	 * @param textOffset
	 *            文章偏移
	 * @return 索引
	 */
	public int matchOne(String pattern, String text, int textOffset) {
		int i;
		/* Preprocessing */
		preBadChar(pattern);
		preGoodSuff(pattern);
		/* Searching */
		while (textOffset <= text.length() - pattern.length()) {
			for (i = pattern.length() - 1; i >= 0 && pattern.charAt(i) == text.charAt(i + textOffset); --i)
				;
			if (i < 0) {
				return textOffset;
			} else{
				/* 是否存在坏字符规则中 */
				int bad;
				if (badChar.containsKey(text.charAt(i + textOffset)))
					bad = badChar.get(text.charAt(i + textOffset)) - pattern.length() + 1 + i;
				else
					bad = 1 + i;
				textOffset += max(goodSuffix[i], bad);
			}
		}
		// 不存在
		return text.length();
	}

	/**
	 * 返回两个数中的最大值
	 * 
	 * @param a
	 * @param b
	 * @return 返回最大值
	 */
	private int max(int a, int b) {
		return a > b ? a : b;
	}
}
