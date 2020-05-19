package com.whataboutmv.modules.account;

import com.querydsl.core.types.Predicate;
import com.whataboutmv.modules.tag.Tag;
import com.whataboutmv.modules.zone.*;


import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return QAccount.account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
