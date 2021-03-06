package com.vwo.mobile.segmentation;

import android.content.Context;

import com.vwo.mobile.TestUtils;
import com.vwo.mobile.VWO;
import com.vwo.mobile.mock.ShadowVWOLog;
import com.vwo.mobile.mock.VWOMock;
import com.vwo.mobile.mock.VWOPersistDataShadow;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.utils.VWOUtils;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

/**
 * Created by aman on Mon 08/01/18 14:28.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, shadows = {VWOPersistDataShadow.class, ShadowVWOLog.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "org.json.*", "com.vwo.mobile.utils.VWOLog"})
@PrepareForTest({VWOUtils.class})
public class SegmentUtilsTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private VWO vwo;

    @Before
    public void setup() {
        vwo = new VWOMock().getVWOMockObject();

        PowerMockito.mockStatic(VWOUtils.class);
        PowerMockito.when(VWOUtils.applicationVersion(any(Context.class))).thenReturn(1);
        PowerMockito.when(VWOUtils.androidVersion()).thenReturn("21");
    }

    @Test
    public void campaignNoSegmentation() throws IOException, JSONException {
        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign_no_segmentation.json");
        Campaign campaign = new Campaign(vwo, new JSONObject(data));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign));
    }

    @Test
    public void test1() throws JSONException, IOException {
        String data = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign1.json");
        Campaign campaign = new Campaign(vwo, new JSONObject(data));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign));
    }

    @Test
    public void test2() throws IOException, JSONException {
        String data2 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign2.json");
        Campaign campaign2 = new Campaign(vwo, new JSONObject(data2));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign2));
    }

    @Test
    public void test3() throws IOException, JSONException {
        // Extra operator
        String data3 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign3.json");
        Campaign campaign3 = new Campaign(vwo, new JSONObject(data3));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign3));
    }

    @Test
    public void test4() throws IOException, JSONException {
        String data4 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign4.json");
        Campaign campaign4 = new Campaign(vwo, new JSONObject(data4));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign4));
    }

    @Test
    public void test5() throws IOException, JSONException {
        String data5 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign5.json");
        Campaign campaign5 = new Campaign(vwo, new JSONObject(data5));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign5));
    }

    @Test
    public void test6() throws IOException, JSONException {
        String data6 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign6.json");
        Campaign campaign6 = new Campaign(vwo, new JSONObject(data6));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign6));
    }

    @Test
    public void test7() throws IOException, JSONException {
        String data7 = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign7.json");
        Campaign campaign7 = new Campaign(vwo, new JSONObject(data7));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign7));
    }

    @Test
    public void test8() throws IOException, JSONException {
        String campaignIdMissing = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign_id_missing.json");
        Campaign campaign9 = new Campaign(vwo, new JSONObject(campaignIdMissing));
        Assert.assertFalse(SegmentUtils.evaluateSegmentation(campaign9));
    }

    @Test
    public void test9() throws IOException, JSONException {
        String campaignStatusMissing = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign_status_missing.json");
        Campaign campaign11 = new Campaign(vwo, new JSONObject(campaignStatusMissing));
        Assert.assertTrue(SegmentUtils.evaluateSegmentation(campaign11));
    }

    @Test
    public void excludedCampaignTest() throws IOException, JSONException {
        String campaignExcluded = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign_excluded.json");

        Campaign campaign8 = new Campaign(vwo, new JSONObject(campaignExcluded));

        Assert.assertFalse(SegmentUtils.evaluateSegmentation(campaign8));

    }

    @Test
    public void pausedCampaignTest() throws JSONException, IOException {
        String campaignPaused = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/campaign_paused.json");
        Campaign campaign10 = new Campaign(vwo, new JSONObject(campaignPaused));

        Assert.assertFalse(SegmentUtils.evaluateSegmentation(campaign10));
    }
}
