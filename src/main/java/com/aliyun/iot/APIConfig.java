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

package com.aliyun.iot;

/**
 * 模块所有api移到此处
 */
public class APIConfig {

    //首页模块start//
    /**
     * 介绍：首页（分组+设备）接口
     * 传参：params:{"groupId":"","pageNo":1,"pageSize":60",propertyIdentifier":["LightSwitch","PowerSwitch","WorkSwitch"]}
     * 版本：1.0.1
     */
    public static final String HOME_DEVICE_AND_GROUP = "/living/devicegroup/devices/query";
    public static final String HOME_DEVICE_AND_GROUP_VERSION = "1.0.1";

    /**
     * 介绍：设备重命名接口
     * 传参：params:{"groupId":"","nickName":""}
     * 版本：1.0.2
     */
    public static final String HOME_DEVICE_RENICKNAME = "/uc/setDeviceNickName";
    public static final String HOME_DEVICE_RENICKNAME_VERSION = "1.0.2";


    /**
     * 介绍：设备解绑接口
     * 传参：params:{"iotId":"","homeId":""}
     * 版本：1.0.7
     */
    public static final String HOME_DEVICE_UNBIND = "/uc/unbindAccountAndDev";
    public static final String HOME_DEVICE_UNBIND_VERSION = "1.0.7";

    /**
     * 网关子设备解绑
     */
    public static final String HOME_DEVICE_SUB_UNBIND = "/awss/subdevice/unbind";
    public static final String HOME_DEVICE_SUB_UNBIND_VERSION = "1.0.7";

    /**
     * 获取网关子设备列表
     */
    public static final String HOME_DEVICE_SUB_DEVICE_LIST = "/subdevices/list";
    public static final String HOME_DEVICE_SUB_DEVICE_LIST_VERSION = "1.0.2";

    /**
     * 介绍：设备分组接口
     * 传参：params:{}
     * 版本：1.0.1
     */
    public static final String HOME_DEVICE_GROUP_LIST = "/living/devicegroup/group/list";
    public static final String HOME_DEVICE_GROUP_LIST_VERSION = "1.0.1";

    /**
     * 介绍：设备分组添加接口
     * 传参：params:{"groupName":""}
     * 版本：1.0.1
     */
    public static final String HOME_DEVICE_GROUP_CREAT = "/living/devicegroup/group/create";
    public static final String HOME_DEVICE_GROUP_CREAT_VERSION = "1.0.1";

    /**
     * 介绍：设备分组删除接口
     * 传参：params:{"groupId":"yFzX5y3EDNilZdFhYadg010100"}
     * 版本：1.0.1
     */
    public static final String HOME_GROUP_DELETE = "/living/devicegroup/group/delete";
    public static final String HOME_GROUP_DELETE_VERSION = "1.0.1";

    /**
     * 介绍：设备分组重命名接口
     * 传参：params:{"groupName":"灯定价","groupId":"UuJhv5fbMITHisJguDMm010100"}
     * 版本：1.0.1
     */
    public static final String HOME_GROUP_RENAME = "/living/devicegroup/group/update";
    public static final String HOME_GROUP_RENAME_VERSION = "1.0.1";

    /**
     * 介绍：创建家
     * 传参：params:{"name":"二雷的家","locationId":"xxx","country":"中国","province":"浙江省","city":"杭州市","district":"西湖区","address":"转塘街道飞天园区"}
     * 版本：1.0.0
     */
    public static final String HOME_MANAGER_CREATE = "/living/home/create";
    public static final String HOME_MANAGER_CREATE_VERSION = "1.0.1";

    /**
     * 介绍：获取家列表
     * 传参：params:{ "pageNo":1,"pageSize":10}
     * 版本：1.0.0
     */
    public static final String HOME_MANAGER_QUERY = "/living/home/query";
    public static final String HOME_MANAGER_QUERY_VERSION = "1.0.0";

    /**
     * 介绍：获取家的详情
     * 传参：params:{ "homeId":"xxx"}
     * 版本：1.0.0
     */
    public static final String HOME_MANAGER_GET = "/living/home/get";
    public static final String HOME_MANAGER_GET_VERSION = "1.0.1";

    /**
     * 介绍：删除家
     * 传参：params:{"homeId":"xxx"}
     * 版本：1.0.0
     */
    public static final String HOME_MANAGER_DELETE = "/living/home/delete";
    public static final String HOME_MANAGER_DELETE_VERSION = "1.0.0";

    /**
     * 介绍：更新家
     * 传参：params:{"homeId":"xxx","name":"二雷的家","locationId":"xxx","country":"中国","province":"浙江省","city":"杭州市","district":"西湖区","address":"转塘街道飞天园区"}
     * 版本：1.0.0
     */
    public static final String HOME_MANAGER_UPDATE = "/living/home/update";
    public static final String HOME_MANAGER_UPDATE_VERSION = "1.0.0";


    /**
     * 查询用户和设备的关系
     */
    public static final String DEVICE_GETACCOUNR_DEV = "/uc/getByAccountAndDev";
    public static final String DEVICE_GETACCOUNR_DEV_VERSION = "1.0.2";

    /**
     * 设备移动
     */
    public static final String HOME_DEIVE_MOVE = "/living/home/device/move";
    public static final String HOME_DEIVE_MOVE_VERSION = "1.0.0";


    /**
     * 介绍：更新用户的配置信息
     * 传参：无
     * 版本：1.0.0
     */
    public static final String HOME_CURRENT_UPDATE = "/living/home/current/update";
    public static final String HOME_CURRENT_UPDATE_VERSION = "1.0.0";


    /**
     * 介绍：获取房间列表
     * 传参：homeId
     * 版本：1.0.0
     */
    public static final String ROOM_MANAGER_QUERY = "/living/home/room/query";
    public static final String ROOM_MANAGER_QUERY_VERSION = "1.0.1";

    /**
     * 房间排序
     */
    public static final String LIVING_HOME_ROOM_REORDER = "/living/home/room/reorder";
    public static final String LIVING_HOME_ROOM_REORDER_VERSION = "1.0.0";


    /**
     * 介绍：创建房间
     * 传参：params:{"homeId":"xxx","name":"二雷的游戏室","backgroudImage":""}
     * 版本：1.0.0
     */
    public static final String ROOM_MANAGER_CREATE = "/living/home/room/create";
    public static final String ROOM_MANAGER_CREATE_VERSION = "1.0.1";


    /**
     * 介绍：删除房间
     * 传参：params:{"homeId":"xxx","roomId":"xxx"}
     * 版本：1.0.0
     */
    public static final String ROOM_MANAGER_DELETE = "/living/home/room/delete";
    public static final String ROOM_MANAGER_DELETE_VERSION = "1.0.0";

    /**
     * 介绍：更新房间
     * 传参：params:{"homeId":"xxx","roomId":"xxx","name":"xxx"}
     * 版本：1.0.0
     */
    public static final String ROOM_MANAGER_UPDATE = "/living/home/room/update";
    public static final String ROOM_MANAGER_UPDATE_VERSION = "1.0.1";

    /**
     * 介绍：获取房间详情
     * 传参：params:{"homeId":"xxx","roomId":"xxx"}
     * 版本：1.0.0
     */
    public static final String ROOM_MANAGER_GET = "/living/home/room/get";
    public static final String ROOM_MANAGER_GET_VERSION = "1.0.1";

    /**
     * 介绍：获取房间下的设备列表
     * 传参：params:{"homeId":"xxx","roomId":"xxx","pageNo":1,"pageSize":10}
     * 版本：1.0.0
     */
    public static final String ROOM_MANAGER_DEVICE_QUERY = "/living/home/room/device/query";
    public static final String ROOM_MANAGER_DEVICE_QUERY_VERSION = "1.0.0";

    /**
     * 介绍：房间排序
     * 传参：params:{"homeId":"xxx","sortingObjectList":[{"objectId":"xxx","fromOrder":1,"toOrder":2}]}
     * 版本：1.0.0
     */
    public static final String ROOM_MANAGER_SORT = "/living/home/room/order/refresh";
    public static final String ROOM_MANAGER_SORT_VERSION = "1.0.0";


    /**
     * 介绍：房间内设备列表更新
     * 传参：params:{"homeId":"xxx","roomId":"xxx","iotIdList":["xxx1","xxx2"]}
     * 版本：1.0.0
     */
    public static final String ROOM_MANAGER_DEVICE_UPDATE = "/living/home/room/device/full/update";
    public static final String ROOM_MANAGER_DEVICE_UPDATE_VERSION = "1.0.0";
    /**
     * 介绍：获取用户维度下的设备列表
     * 传参：
     * deviceType  否 根据设备类型过滤，可取值：DEVICE，GATEWAY
     * myRole  否 用户和设备的归属关系，OWNED 表示管理员，SHARED 表示被分享者
     * 版本：1.0.0
     */
    public static final String USER_DEVICE_QUERY = "/living/device/query";
    public static final String USER_DEVICE_QUERY_VERSION = "1.0.0";

    /**
     * 介绍：获取家下的全部设备
     * 传参：homeId，pageNo，pageSize
     * 版本：1.0.0
     */
    public static final String HOME_DEVICE_QUERY = "/living/home/element/query";
    public static final String HOME_DEVICE_QUERY_VERSION = "1.0.1";

    /**
     * 介绍：家下面的全部设备 + 控制组排序
     * 传参：homeId，sortingObjectList
     * 版本：1.0.0
     */
    public static final String HOME_DEVICE_REORDER = "/living/home/element/reorder";
    public static final String HOME_DEVICE_REORDER_VERSION = "1.0.0";

    /**
     * 介绍：获取家天气
     * 传参：locationId
     * 版本：1.0.0
     */
    public static final String HOME_WEATHER_CURRENT_GET = "living/weather/current/get";
    public static final String HOME_WEATHER_CURRENT_GET_VERSION = "1.0.0";

    /**
     * 组控控制
     */
    public static final String CONTROLGROUP_BATCH_SET = "/living/controlgroup/batch/set";
    public static final String CONTROLGROUP_BATCH_SET_VERSION = "1.0.0";


    /**
     * 房间下面的设备排序
     */
    public static final String HOME_ROOM_DEIVE_REORDER = "/living/home/room/device/reorder";
    public static final String HOME_ROOM_DEIVE_REORDER_VERSION = "1.0.0";

    /**
     * 查询共享设备列表
     */
    public static final String USER_DEVICE_SHARED_QUERY = "/living/user/device/shared/query";
    public static final String USER_DEVICE_SHARED_QUERY_VERSION = "1.0.0";

    /**
     * 首页查询共享设备总数&在线数
     */
    public static final String LIVING_USER_PROFILE_GET = "/living/user/profile/get";
    public static final String LIVING_USER_PROFILE_GET_VERSION = "1.0.0";

    /**
     * 家下的设备列表排序
     */
    public static final String LIVING_HOME_DEVICE_REORDER = "/living/home/device/reorder";
    public static final String LIVING_HOME_DEVICE_REORDER_VERSION = "1.0.0";

    /**
     * 共享设备排序
     */
    public static final String LIVING_USER_DEVICE_SHARED_REORDER = "/living/user/device/shared/reorder";
    public static final String LIVING_USER_DEVICE_SHARED_REORDER_VERSION = "1.0.0";


    //首页模块end//

    //控制组//
    /**
     * 查询家下的控制组列表
     * 传参数：
     * argus.put("homeId", homeId);
     * argus.put("pageNo", pageNo);
     * argus.put("pageSize", pageSize);
     * 版本：1.0.0
     */
    public static final String HOME_CONTROLGROUP_QUERY = "/living/home/controlgroup/query";
    public static final String HOME_CONTROLGROUP_QUERY_VERSION = "1.0.0";

    /**
     * 查询控制组中设备列表
     * 传参数：
     * argus.put("homeId", homeId);
     * argus.put("controlGroupId", controlGroupId);
     * argus.put("pageNo", pageNo);
     * argus.put("pageSize", pageSize);
     * JSONArray propertyIdentifiers = JSONObject.parseArray("[\"LightSwitch\",\"PowerSwitch\",\"WorkSwitch\"]");
     * argus.put("propertyIdentifiers", propertyIdentifiers);
     * 版本：1.0.1
     */
    public static final String HOME_CONTROLGROUP_DEVICE_QUERY = "/living/home/controlgroup/device/query";
    public static final String HOME_CONTROLGROUP_DEVICE_QUERY_VERSION = "1.0.2";

    /**
     * 创建控制组
     * 传参数：
     * argus.put("homeId", homeId);
     * argus.put("name", name);
     * argus.put("productKey", productKey);
     * argus.put("iotIdList", iotIdList);
     * argus.put("enableLocalControl", true);
     * 版本：1.0.1
     */
    public static final String HOME_CONTROLGROUP_CREATE = "/living/home/controlgroup/create";
    public static final String HOME_CONTROLGROUP_CREATE_VERSION = "1.0.1";


    /**
     * 批量删除控制组
     */
    public static final String HOME_CONTROLGROUP_BARTCH_DELETE = "/living/home/controlgroup/batch/delete";
    public static final String HOME_CONTROLGROUP_BARTCH_DELETE_VERSION = "1.0.0";

    /**
     * 更新控制组中的设备
     * 传参数：
     * argus.put("homeId", homeId);
     * argus.put("controlGroupId", controlGroupId);
     * argus.put("iotIdList", iotIdList);
     * 版本：1.0.0
     */
    public static final String HOME_CONTROLGROUP_DEVICE_FULL_UPDATE = "/living/home/controlgroup/device/full/update";
    public static final String HOME_CONTROLGROUP_DEVICE_FULL_UPDATE_VERSION = "1.0.0";

    /**
     * 更新控制组
     * 用途：目前用于更新控制组名称
     * 传参数：
     * argus.put("homeId", homeId);
     * argus.put("controlGroupId", controlGroupId);
     * argus.put("name", name);
     * 版本：1.0.0
     */
    public static final String HOME_CONTROLGROUP_UPDATE = "/living/home/controlgroup/update";
    public static final String HOME_CONTROLGROUP_UPDATE_VSERION = "1.0.0";

    /**
     * 删除设备组
     * 传参数：
     * argus.put("homeId", homeId);
     * argus.put("controlGroupId", controlGroupId);
     * 版本：1.0.0
     */
    public static final String HOME_CONTROLGROUP_DELETE = "/living/home/controlgroup/delete";
    public static final String HOME_CONTROLGROUP_DELETE_VSERION = "1.0.0";

    /**
     * 删除设备组
     * 传参数：
     * argus.put("homeId", homeId);
     * argus.put("controlGroupId", controlGroupId);
     * argus.put("targetOrder", targetOrder);
     * 版本：1.0.0
     */
    public static final String HOME_CONTROLGROUP_REORDER = "/living/home/controlgroup/reorder";
    public static final String HOME_CONTROLGROUP_REORDER_VSERION = "1.0.0";
    //控制组end//


    //配网模块start provision//
    /**
     * 介绍：设备只有productId时，需要用pid还pk（主要时延陵那边本地发现的设备）
     * 传参：params:{"productId":""}
     * 版本：1.1.4
     */
    public static final String PROVISION_DEVICE_PIDTOPK = "/thing/productInfo/queryProductKey";
    public static final String PROVISION_DEVICE_PIDTOPK_VERSION = "1.1.4";

    /**
     * 介绍：本地发现云端过滤接口
     * 传参：params:{"iotDevices":[{"productKey":"","deviceName":""}]}
     * 版本：1.0.2
     */
    public static final String PROVISION_DEVICE_FILTER = "/awss/enrollee/product/filter";
    public static final String PROVISION_DEVICE_FILTER_VERSION = "1.0.2";

    /**
     * 介绍：扫码获取产品信息接口
     * 传参：params:{"productkey":""}
     * 版本：1.1.4
     */
    public static final String PROVISION_DEVICE_INFO = "/thing/allProductInfo/getByProductKey";
    public static final String PROVISION_DEVICE_INFO_VERSION = "1.1.4";

    /**
     * 介绍：手动添加页左侧接口
     * 传参：params:{}
     * 版本：1.0.0
     */
    public static final String PROVISION_CATEGORY_ROOT_LIST = "/living/awss/enrollee/category/root/list";
    public static final String PROVISION_CATEGORY_ROOT_LIST_VERSION = "1.0.0";

    /**
     * 介绍：手动添加页右侧接口
     * 传参：params:{"superId":"58"}
     * 版本：1.0.0
     */
    public static final String PROVISION_CATEGORY_CHILD_LIST = "/living/awss/enrollee/category/child/list";
    public static final String PROVISION_CATEGORY_CHILD_LIST_VERSION = "1.0.0";
    //海外版
    public static final String PROVISION_CATEGORY_CHILD_OVERSEAS_LIST = "/living/awss/enrollee/category/pickedchild/list";
    public static final String PROVISION_CATEGORY_CHILD_OVERSEAS_LIST_VERSION = "1.0.2";

    /**
     * 介绍：产品列表接口
     * 传参：params:{"pageNo":1,"categoryKey":"Light","pageSize":1000}
     * 版本：1.0.2
     */
    public static final String PROVISION_PRODUCT_LIST = "/awss/enrollee/product/list";
    public static final String PROVISION_PRODUCT_LIST_VERSION = "1.0.2";

    /**
     * 介绍：获取配网引导信息接口
     * 传参：params:{"productKey":"a1w5jpbQMO4"}
     * 版本：1.1.5
     */
    public static final String PROVISION_GUIDE_INFO = "/awss/enrollee/guide/get";
    public static final String PROVISION_GUIDE_INFO_VERSION = "1.1.5";

    /**
     * 介绍：绑定接口(时间窗口)设备类型：NET_CELLULAR、NET_ZIGBEE、NET_OTHER、NET_BT
     * 传参：params:{"productKey":"","deviceName":"","token":""}
     * 版本：1.0.7
     */
    public static final String PROVISION_DEVICE_TIME_BIND = "/awss/time/window/user/bind";
    public static final String PROVISION_DEVICE_TIME_BIND_VERSION = "1.0.8";

    /**
     * 介绍：绑定接口(token)设备类型：NET_WIFI、NET_ETHERNET
     * 传参：params:{"productKey":"","deviceName":"","token":""}
     * 版本：1.0.7
     */
    public static final String PROVISION_DEVICE_TOKEN_BIND = "awss/token/user/bind";
    public static final String PROVISION_DEVICE_TOKEN_BIND_VERSION = "1.0.8";
    //配网模块end//

    //智能模块start//

    /**接口文档：
     * https://living.aliyun.com/doc?spm=a2c9o.12549863.0.0.10fd38e4CIxxsO#tad3o3.html
     * https://yuque.antfin-inc.com/ilopdoc/design/rc6wbe
     * https://yuque.antfin-inc.com/ilopdoc/design/esxfg0
     * https://yuque.antfin-inc.com/ilopdoc/design/xzgnwv#0a4970ea
     * EMP文档：
     * https://yuque.antfin-inc.com/iot-emp/gzx4tu/od6lpg
     * https://yuque.antfin-inc.com/docs/share/9ac351cd-db2b-4100-aa90-63414c5db16e
     * https://yuque.antfin-inc.com/docs/share/7e25468c-b1e8-442f-bd83-79c268c7c1b2
     * https://yuque.antfin-inc.com/docs/share/48fd734d-c440-457e-8930-5f0b1054ee2f
     * **/

    /**
     * 介绍：创建智能模块
     * 传参：homeId
     * 版本：1.0.1
     */
    public static final String INTELLIGENCE_CREATE_SCENE = "/living/scene/create";
    public static final String INTELLIGENCE_CREATE_SCENE_VERSION = "1.0.1";
    /**
     * 介绍：智能页(场景+自动化)列表接口
     * 传参：params:{"pageNo":1,"pageSize":20,"homeId":""}
     * 版本：1.0.2
     */
    public static final String INTELLIGENCE_ALL_LIST = "/scene/list/all";
    public static final String INTELLIGENCE_ALL_LIST_VERSION = "1.0.2";

    /**
     * 介绍：
     * 传参：
     * 版本：1.0.0
     */
    public static final String INTELLIGENCE_UPDATE_SCENE_SWITCH = "/living/scene/switch";
    public static final String INTELLIGENCE_UPDATE_SCENE_SWITCH_VERSION = "1.0.0";

    /**
     * 介绍：
     * 传参：
     * 版本：1.0.5
     */
    public static final String INTELLIGENCE_REORDER_SCENE = "/scene/list/reorder";
    public static final String INTELLIGENCE_REORDER_SCENE_VERSION = "1.0.6";

    /**
     * 介绍：场景执行接口
     * 传参：
     * 版本：1.0.2
     */
    public static final String INTELLIGENCE_FIRE_SCENE = "/scene/fire";
    public static final String INTELLIGENCE_FIRE_SCENE_VERSION = "1.0.2";

    /**
     * 介绍：场景执行失败日志接口
     * 传参：
     * 版本：1.0.4
     */
    public static final String INTELLIGENCE_GET_SCENE_FAIL_LOG_DETAIL = "/scene/failedlog/get";
    public static final String INTELLIGENCE_GET_SCENE_FAIL_LOG_DETAIL_VERSION = "1.0.4";

    /**
     * 接口文档：https://yuque.antfin-inc.com/docs/share/b9a447c6-0a38-4dd2-941d-90557c3eb6f9?#jFCg5
     * 用途:用来查询延迟执触发自动化日志的。
     * 获取场景详细日志列表
     * 传参：{
     * "sceneId": "1234",
     * "logId": "1234",
     * "pageNo": 1,
     * "pageSize": 10,
     * "time": 1578296985000
     * }
     * <p>
     * 版本：1.0.0
     */
    public static final String LIVING_SCENE_DETAILLOG_QUERY = "/living/scene/log/detail/query";
    public static final String LIVING_SCENE_DETAILLOG_QUERY_VERSION = "1.0.0";


    /**
     * 介绍：
     * 传参：
     * 版本：1.0.2
     */
    public static final String INTELLIGENCE_GET_SCENE_THING_ABILITY = "/iotid/scene/ability/list";
    public static final String INTELLIGENCE_GET_SCENE_THING_ABILITY_VERSION = "1.0.2";

    /**
     * 介绍：
     * 传参：
     * 版本：1.0.3
     */
    public static final String INTELLIGENCE_GET_SCENE_NOTICE_THING_LIST = "/scene/enable/notice/alarm/device/list";
    public static final String INTELLIGENCE_GET_SCENE_NOTICE_THING_LIST_VERSION = "1.0.3";

    /**
     * 介绍：
     * 传参：
     * 版本：1.0.3
     */
    public static final String INTELLIGENCE_GET_SCENE_NOTICE_THING_ABILITY = "/scene/device/alarm/info/list";
    public static final String INTELLIGENCE_GET_SCENE_NOTICE_THING_ABILITY_VERSION = "1.0.3";

    /**
     * 介绍：智能页(场景+自动化)列表更新接口
     * 传参：params:{"catalogId":"0","pageNo":1,"pageSize":20,"homeId",""}
     * 版本：1.0.2
     */
    public static final String INTELLIGENCE_SINGLE_LIST = "/living/scene/query";
    public static final String INTELLIGENCE_SINGLE_LIST_VERSION = "1.0.1";

    /**
     * 介绍：场景+自动化详情页接口(catalogId="0"-->场景 或"1"-->自动化）
     * 传参：params:{"catalogId":"0","sceneId":"6fe062582607405197c26e010608b72f"}
     * 版本：1.0.0
     */
    public static final String INTELLIGENCE_SCENE_INFO = "/living/scene/info/get";
    public static final String INTELLIGENCE_SCENE_INFO_VERSION = "1.0.0";

    /**
     * 介绍：场景设备列表接口
     * 传参： params:{"pageSize":20,"pageNum":1,"flowType":2}
     * 版本：1.0.5
     */
    public static final String INTELLIGENCE_SCENE_DEVICE_LIST = "/scene/thing/list";
    public static final String INTELLIGENCE_SCENE_DEVICE_LIST_VERSION = "1.0.5";

    /**
     * 介绍：智能设备tsl信息接口
     * 传参：params:{"iotId":"PVAZ3ykKtdfRHe9piDQQ000100","flowType":2}
     * 版本：1.0.2
     */
    public static final String INTELLIGENCE_DEVICE_TSL_INFO = "/iotid/scene/ability/tsl/list";
    public static final String INTELLIGENCE_DEVICE_TSL_INFO_VERSION = "1.0.2";

    /**
     * 介绍：智能更新接口
     * 场景传参：params:{"catalogId":"0","enable":true,"reBind":false,"sceneId":"6fe062582607405197c26e010608b72f","name":"关啊","icon":"http://gaic.alicdn.com/ztms/cloud-intelligence-icons/icons/scene_img_choice_icon_11.png","iconColor":"#A86AFB","actions":[{"params":{"iotId":"uOmYtL3PzsnPAz74LaHe000100","propertyName":"PowerSwitch","propertyValue":0},"uri":"action/device/setProperty"},{"params":{"iotId":"PVAZ3ykKtdfRHe9piDQQ000100","propertyName":"LightSwitch","propertyValue":0},"uri":"action/device/setProperty"}]}
     * 自动化传参：params:{"mode":"all","catalogId":"1","sceneType":"CA","enable":false,"caConditions":[{"params":{"cron":"0 7 22 23 8 ? 2019","cronType":"quartz_cron","timezoneID":"Asia/Shanghai"},"uri":"condition/timer"},{"params":{"unitType":"C","compareValue":"15","compareType":"<","locationName":"杭州","locationId":"106832"},"uri":"condition/ilop/weather/temperature"}],"reBind":false,"sceneId":"4005854f0eea4572b39577f79fe3748b","name":"哈哈哈哈-电源开关 - 关闭","icon":"http://gaic.alicdn.com/ztms/cloud-intelligence-icons/icons/scene_img_choice_icon_2.png","iconColor":"#56DD1F","actions":[{"params":{"iotId":"uOmYtL3PzsnPAz74LaHe000100","propertyName":"PowerSwitch","propertyValue":0},"uri":"action/device/setProperty"}]}
     * 版本：1.0.0
     */
    public static final String INTELLIGENCE_SCENE_UPDATE = "/living/scene/update";
    public static final String INTELLIGENCE_SCENE_UPDATE_VERSION = "1.0.0";

    /**
     * 介绍：智能删除接口
     * 场景传参：params:{"sceneId":"6fe062582607405197c26e010608b72f"}
     * 版本：1.0.0
     */
    public static final String INTELLIGENCE_DELETE = "/living/scene/delete";
    public static final String INTELLIGENCE_DELETE_VERSION = "1.0.0";

    /**
     * 介绍：自动化位置获取接口
     * 传参：params:{"queryPattern":"杭州"}
     * 版本：1.0.0
     */
    public static final String INTELLIGENCE_AUTO_LOCATION_INFO = "/living/scene/weather/location/query";
    public static final String INTELLIGENCE_AUTO_LOCATION_INFO_VERSION = "1.0.0";

    /**
     * 根据gps获取位置信息。
     * 传参：params:{}
     * 版本：1.0.0
     */
    public static final String INTELLIGENCE_AUTO_GPS_LOCATION_INFO = "/living/scene/weather/location/get";
    public static final String INTELLIGENCE_AUTO_GPS_LOCATION_INFO_VERSION = "1.0.0";

    /**
     * 检查场景 Action 内是否存在离线设备
     * 传参:sceneId
     * 版本:1.0.0
     */
    public static final String INTELLIGENCE_SCENE_DEVICE_OFFLINE_CHECK = "/living/scene/device/offline/check";
    public static final String INTELLIGENCE_SCENE_DEVICE_OFFLINE_CHECK_VERSION = "1.0.0";

    //智能模块end//

    //我的模块start//
    /**
     * 介绍：
     * 传参：params:{}
     * 版本：1.0.1
     */
    public static final String ME_REDPOINT_INFO = "/feedback/redpoint/get";
    public static final String ME_REDPOINT_INFO_VERSION = "1.0.1";

    /**
     * 介绍：未读消息+待升级固件数量
     * 传参：params:{}
     * 版本：1.0.2
     */
    public static final String ME_REMINDING_INFO = "/uc/system/reminding/get";
    public static final String ME_REMINDING_INFO_VERSION = "1.0.2";

    /**
     * 介绍：修改头像接口
     * 传参：params:{"ossKey":"iotx_uc/images/user/profile/1450572514.png","action":"PUT","ossProfile":"app_avanter"}
     * 版本：1.0.1
     */
    public static final String ME_HEAD_UPDATE = "/app/oss/operate";
    public static final String ME_HEAD_UPDATE_VERSION = "1.0.1";

    //小组件start
    /**
     * 介绍：小组件场景列表接口
     * 传参：params:{}
     * 版本：1.0.0
     */
    public static final String ME_APPWIDGET_SCENE_LIST = "/living/appwidget/list";
    public static final String ME_APPWIDGET_SCENE_LIST_VERSION = "1.0.0";

    /**
     * 介绍：小组件场景保存接口
     * 传参：params:{"sceneIds":["8fe4942d6be742478406c466f19ae46d"]}
     * 版本：1.0.0
     */
    public static final String ME_APPWIDGET_SCENE_UPDATE = "/living/appwidget/create";
    public static final String ME_APPWIDGET_SCENE_UPDATE_VERSION = "1.0.0";

    /**
     * 介绍：小组件设备列表接口
     * 传参：params:{}
     * 版本：1.0.0
     */
    public static final String ME_APPWIDGET_PRODUCT_LIST = "/iotx/ilop/queryComponentProduct";
    public static final String ME_APPWIDGET_PRODUCT_LIST_VERSION = "1.0.0";

    /**
     * 介绍：查询小组件可设备绑定的接口
     * 传参：params:{"offset":0,"limit":30,"dataType":"BOOL"}
     * 版本：1.0.0
     */
    public static final String ME_APPWIDGET_BINDING_PRODUCT_LIST = "/iotx/ilop/queryBindingProduct";
    public static final String ME_APPWIDGET_BINDING_PRODUCT_LIST_VERSION = "1.0.0";

    /**
     * 介绍：查询设备属性
     * 传参：params:{"productKey":"","iotId":"","query":{"dataType":"BOOL"}}
     * 版本：1.0.0
     */
    public static final String ME_APPWIDGET_DEVICE_PROPERTY = "/iotx/ilop/queryComponentProperty";
    public static final String ME_APPWIDGET_DEVICE_PROPERTY_VERSION = "1.0.3";


    /**
     * 介绍：小组件设备保存接口
     * 传参： params:{"componentProductList":[{"iotId":"egweXTy2MIuAqcbMLJky000100","iconUrl":"http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1559630606671.png","productKey":"a1ej5yPWU5z","deviceName":"VD_8tA9lLMD8J","productName":"测试BLE产品","properties":[{"propertyName":"电源开关","propertyIdentifier":"PowerSwitch"}],"deviceStatus":1}]}
     * 版本：1.0.0
     */
    public static final String ME_APPWIDGET_PRODUCT_UPDATE = "/iotx/ilop/updateComponentProduct";
    public static final String ME_APPWIDGET_PRODUCT_UPDATE_VERSION = "1.0.0";
    //小组件end

    //消息中心start
    /**
     * 介绍：消息中心消息列表接口
     * 传参：params:{"requestDTO":{"maxId":9223372036854775807,"messageType":"device","sortType":2,"pageNo":1,"pageSize":20,"type":"MESSAGE","minId":0}}
     * params:{"requestDTO":{"maxId":9223372036854775807,"messageType":"announcement","sortType":2,"pageNo":1,"pageSize":20,"type":"NOTICE","minId":0}}
     * 版本：1.0.6
     */
    public static final String ME_MESSAGE_LIST = "/message/center/record/query";
    public static final String ME_MESSAGE_LIST_VERSION = "1.0.7";

    /**
     * 介绍：消息类型条数接口
     * 传参： params:{"requestDTO":{"isRead":0,"type":"NOTICE"}}
     * params:{"requestDTO":{"isRead":0,"type":"MESSAGE"}}
     * 版本：1.0.1
     */
    public static final String ME_MESSAGE_TYPE_COUNT = "/message/center/record/messagetype/count";
    public static final String ME_MESSAGE_TYPE_COUNT_VERSION = "1.0.1";

    /**
     * 介绍：未读消息数量
     * 传参：params:{"requestDTO":{"messageType":"device","isRead":1,"type":"MESSAGE","minId":0}}
     * params:{"requestDTO":{"messageType":"share","isRead":1,"type":"NOTICE","minId":0}}
     * 版本：1.0.1
     */
    public static final String ME_MESSAGE_RECORD_MODIFY = "/message/center/record/modify";
    public static final String ME_MESSAGE_RECORD_MODIFY_VERSION = "1.0.1";

    /**
     * 介绍：共享消息接口
     * 传参：params:{"pageNo":1,"pageSize":20}
     * 版本：1.0.2
     */
    public static final String ME_MESSAGE_SHARE_LIST = "/uc/getShareNoticeList";
    public static final String ME_MESSAGE_SHARE_LIST_VERSION = "1.0.2";

    /**
     * 介绍：设备消息清空接口
     * 传参：params:{"requestDTO":{"messageType":"device","type":"MESSAGE","minId":0}}
     * params:{"requestDTO":{"messageType":"announcement","type":"NOTICE","minId":0}}
     * 版本：1.0.1
     */
    public static final String ME_MESSAGE_LIST_CLEAR = "/message/center/record/delete";
    public static final String ME_MESSAGE_LIST_CLEAR_VERSION = "1.0.1";

    /**
     * 介绍：分享消息清空接口
     * 传参：params:{}
     * 版本：1.0.2
     */
    public static final String ME_MESSAGE_SHARE_LIST_CLEAR = "/uc/clearShareNoticeList";
    public static final String ME_MESSAGE_SHARE_LIST_CLEAR_VERSION = "1.0.2";
    //消息中心end

    //智能日志start
    /**
     * 介绍：智能日志列表接口
     * 传参：params:{"pageNo":1,"pageSize":20,"nowTime":1567650486444}
     * 版本：1.0.5
     */
    public static final String ME_INTELLIGENCE_LOG_LIST = "/scene/log/list/get";
    public static final String ME_INTELLIGENCE_LOG_LIST_VERSION = "1.0.6";
    //智能日志end

    //设备共享start
    /**
     * 介绍：我的设备+共享设备列表接口
     * 传参：params:{"pageNo":1,"owned":1,"pageSize":20}
     * params:{"pageNo":1,"owned":0,"pageSize":20}
     * 版本：1.0.3
     */
    public static final String ME_DEVICE_SHARE_DEVICE_LIST = "/uc/listBindingByAccount";
    public static final String ME_DEVICE_SHARE_DEVICE_LIST_VERSION = "1.0.3";

    /**
     * 介绍：单个+批量（手机+邮箱）设备分享接口
     * 传参：params:{"mobileLocationCode":"86","accountAttrType":"MOBILE","accountAttr":"18225600623","iotIdList":["l3xyqkXBNzrKElIVl5sh000100","eTHOkPOfI4MgEgQib7xy000100"]}
     * params:{"mobileLocationCode":"","accountAttrType":"EMAIL","accountAttr":"406904512@msn.com","iotIdList":["l3xyqkXBNzrKElIVl5sh000100","eTHOkPOfI4MgEgQib7xy000100"]}
     * 版本：1.0.3
     */
    public static final String ME_DEVICE_SHARE_EXECUTE = "/uc/shareDevicesAndScenes";
    public static final String ME_DEVICE_SHARE_EXECUTE_VERSION = "1.0.3";
    //设备共享end

    //更多服务start
    /**
     * 介绍：支持天猫精灵+亚马逊+googleHome+IFTT设备列表接口
     * 传参：params:{"pageNo":1,"channel":"TmallGenie","pageSize":30}
     * params:{"pageNo":1,"channel":"Echo","pageSize":30}
     * params:{"pageNo":1,"channel":"GoogleHome","pageSize":30}
     * params:{"pageNo":1,"channel":"IFTTT","pageSize":30}
     * 版本：1.0.4
     */
    public static final String ME_THIRD_SUPPORT = "/device/thirdsupport/get";
    public static final String ME_THIRD_SUPPORT_VERSION = "1.0.4";

    /**
     * 介绍：
     * 传参：params:{"accountType":"TAOBAO"}
     * 版本：1.0.5
     */
    public static final String ME_THIRD_SUPPORT_ACCOUNT = "/account/thirdparty/get";
    public static final String ME_THIRD_SUPPORT_ACCOUNT_VERSION = "1.0.5";

    public static final String ME_THIRD_TAOBAO_BIND_ACCOUNT = "/account/taobao/bind";
    public static final String ME_THIRD_TAOBAO_BIND_ACCOUNT_VERSION = "1.0.5";

    public static final String ME_THIRD_BIND_UNBIND_ACCOUNT = "/account/thirdparty/unbind";
    public static final String ME_THIRD_BIND_UNBIND_ACCOUNT_VERSION = "1.0.5";

    //更多服务end

    //设置start
    //---------固件升级start
    /**
     * 介绍：待升级固件列表接口
     * 传参：params:{"houseId":""}
     * 版本：1.0.2
     */
    public static final String ME_OTA_LIST = "/thing/ota/listByUser";
    public static final String ME_OTA_LIST_VERSION = "1.0.2";

    /**
     * 介绍：Wi-Fi设备开始升级接口
     * 传参：params:{"iotIds":[]}
     * 版本：1.0.2
     */
    public static final String ME_OTA_WIFI_START = "/thing/ota/batchUpgradeByUser";
    public static final String ME_OTA_WIFI_START_VERSION = "1.0.2";

    /**
     * 介绍：OTA升级状态
     * 传参：params:{}
     * 版本：1.0.2
     */
    public static final String ME_OTA_PROGRESS = "/thing/ota/info/progress/getByUser";
    public static final String ME_OTA_PROGRESS_VERSION = "1.0.2";

    /**
     * 介绍：固件升级详情接口
     * 传参：params:{"iotId":""}
     * 版本：1.1.1
     */
    public static final String ME_OTA_PRODUCT_INFO = "/thing/detailInfo/queryProductInfo";
    public static final String ME_OTA_PRODUCT_INFO_VERSION = "1.1.1";

    /**
     * 介绍：
     * 传参：params:{"productKey":""}
     * 版本：1.1.1
     */
    public static final String ME_OTA_PRODUCT_KEY_INFO = "/thing/detailInfo/queryProductInfoByProductKey";
    public static final String ME_OTA_PRODUCT_KEY_INFO_VERSION = "1.1.1";

    /**
     * 介绍：获取Mac地址
     * 传参：params:{"iotId":"","dataKey":"MAC"}
     * 版本：1.0.2
     */
    public static final String ME_OTA_GET_MAC = "/thing/extended/property/get";
    public static final String ME_OTA_GET_MAC_VERSION = "1.0.2";

    /**
     * 介绍：根据PK和DN获取对应设备升级固件的接口
     * 传参：params:{"productKey": "a1PB5fpXFJn","deviceName": "868575026974305","iotId": "621c73c57b45428190bad85a6206c407"}
     * 版本：1.0.2
     */
    public static final String ME_OTA_GET_Firmware = "/living/ota/firmware/get";
    public static final String ME_OTA_GET_Firmware_VERSION = "1.0.0";
    //---------固件升级end
    //设置end
    //我的模块end


}

