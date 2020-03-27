package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.ability.ScreenAbility;
import com.bizzan.bitrade.entity.QSign;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * @author Shaoxianjun
 * @Description:
 * @date 2019/5/315:53
 */
@Data
public class SignScreen implements ScreenAbility {

    private String unit;

    @Override
    public ArrayList<BooleanExpression> getBooleanExpressions() {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        if (StringUtils.isNotBlank(unit)) {
            booleanExpressions.add(QSign.sign.coin.unit.eq(unit));
        }
        return booleanExpressions;
    }
}
