/*
 *   Copyright (c) 2020 Sinyuk
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.aliyun.iot.ilop.page.mine;

import com.aliyun.iot.aep.sdk.apiclient.adapter.APIGatewayHttpAdapterImpl;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.framework.config.GlobalConfig;

public class SdkFrameworkUtils {
    public static String getAppKey() {
        String securityIndex = getAuthCode();
        return APIGatewayHttpAdapterImpl.getAppKey(AApplication.getInstance().getApplicationContext(), securityIndex);

    }

    public static String getAuthCode() {
        return GlobalConfig.getInstance().getAuthCode();
    }
}
