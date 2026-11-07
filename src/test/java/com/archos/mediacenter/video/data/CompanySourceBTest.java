package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import android.app.Application;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class CompanySourceBTest
{
    @Test public void productionCompaniesStaySeparateFromNetworkAndUnverifiedLegacyStudio() throws Exception
    {
        JSONObject details = new JSONObject("{id:123,name:'Synthetic series',studios:'WingNut Films',"
                + "networks:[{id:1,name:'CBS'}],production_companies:[{id:2,name:'Actual producer'}]}");
        assertEquals(List.of("Actual producer"), DetailsFacts.read("tv", 123, details, null, List.of(), "GB")
                .companies());
    }

    @Test public void matchingNumericIdsDoNotAllowAMoviePayloadToSupplySeriesCompanies() throws Exception
    {
        JSONObject movie = new JSONObject("{id:123,title:'Synthetic movie',"
                + "production_companies:[{id:1,name:'WingNut Films'}]}");
        assertThrows(IllegalArgumentException.class,
                () -> DetailsFacts.read("tv", 123, movie, null, List.of(), "GB"));
        assertThrows(IllegalArgumentException.class,
                () -> DetailsFacts.read("movie", 456, movie, null, List.of(), "GB"));
    }
}
