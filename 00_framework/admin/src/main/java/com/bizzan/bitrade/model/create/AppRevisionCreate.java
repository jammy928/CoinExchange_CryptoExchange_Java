package com.bizzan.bitrade.model.create;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import com.bizzan.bitrade.ability.CreateAbility;
import com.bizzan.bitrade.constant.Platform;
import com.bizzan.bitrade.entity.AppRevision;

import javax.validation.constraints.NotNull;

/**
 * @author Shaoxianjun
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/2416:33
 */
@Data
public class AppRevisionCreate implements CreateAbility<AppRevision> {

    private String remark;

    @NotBlank
    private String version;

    private String downloadUrl;

    @NotNull
    private Platform platform;

    //转化
    @Override
    public AppRevision transformation() {
        AppRevision appRevision = new AppRevision();
        appRevision.setRemark(this.remark);
        appRevision.setVersion(this.version);
        appRevision.setDownloadUrl(this.downloadUrl);
        appRevision.setPlatform(this.platform);
        return appRevision;
    }
}
