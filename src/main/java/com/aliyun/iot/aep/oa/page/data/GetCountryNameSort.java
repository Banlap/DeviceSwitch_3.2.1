package com.aliyun.iot.aep.oa.page.data;

import com.alibaba.sdk.android.openaccount.ui.model.CountrySort;
import com.alibaba.sdk.android.openaccount.ui.util.CharacterParserUtil;
import com.alibaba.sdk.android.openaccount.ui.util.LocaleUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by feijie.xfj on 18/5/24.
 */
public class GetCountryNameSort {
    CharacterParserUtil characterParser = CharacterParserUtil.getInstance();
    String chReg = "[\\u4E00-\\u9FA5]+";

    public GetCountryNameSort() {
    }

    public String getSortLetter(String name) {
        String letter = "#";
        if (name == null) {
            return letter;
        } else {
            String pinyin = this.characterParser.getSelling(name);
            String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);
            if (sortString.matches("[A-Z]")) {
                letter = sortString.toUpperCase(Locale.CHINESE);
            }

            return letter;
        }
    }

    public String getSortLetterBySortKey(String sortKey) {
        if (sortKey != null && !"".equals(sortKey.trim())) {
            String letter = "#";
            String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
            if (sortString.matches("[A-Z]")) {
                letter = sortString.toUpperCase(Locale.CHINESE);
            }

            return letter;
        } else {
            return null;
        }
    }

    public List<CountrySort> search(String str, List<CountrySort> list) {
        if (list == null) {
            return null;
        } else {
            List<CountrySort> filterList = new ArrayList();
            String locale;
            Iterator var5;
            CountrySort country;
            if (str.matches("^([0-9]|[/+]).*")) {
                locale = str.replaceAll("\\-|\\s", "");
                var5 = list.iterator();

                while (true) {
                    do {
                        do {
                            do {
                                if (!var5.hasNext()) {
                                    return filterList;
                                }

                                country = (CountrySort) var5.next();
                            } while (country.name == null);
                        } while (country.code == null);
                    } while (!country.code.contains(locale) && !country.displayName.contains(str));

                    if (!"*".equals(country.sortLetters)) {
                        filterList.add(country);
                    }
                }
            } else {
                locale = LocaleUtils.getCurrentLocale();
                var5 = list.iterator();

                while (true) {
                    do {
                        do {
                            if (!var5.hasNext()) {
                                return filterList;
                            }

                            country = (CountrySort) var5.next();
                        } while (country.code == null);
                    } while (country.name == null);

                    String lowerCaseStr = str.toLowerCase(Locale.CHINESE);
                    if ((country.name.toLowerCase(Locale.CHINESE).contains(lowerCaseStr) || country.sortWeightKey.toLowerCase(Locale.CHINESE).replace(" ", "").contains(lowerCaseStr) || country.englishName.toLowerCase(Locale.CHINESE).contains(lowerCaseStr) || country.domain.toLowerCase(Locale.CHINESE).contains(lowerCaseStr)) && !"*".equals(country.sortLetters)) {
                        filterList.add(country);
                    }

                    if (LocaleUtils.isZHLocale(locale) && country.pinyin.toLowerCase(Locale.CHINESE).contains(lowerCaseStr)) {
                        filterList.add(country);
                    }
                }
            }
        }
    }
}
