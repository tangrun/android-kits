package page;

import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tangrun.kits.page.*;

public class PageTest {

    public void test(AppCompatActivity activity, EditText editText, SmartRefreshLayout refreshLayout){
        PagerForSmartRefreshLayout build = new PagerForSmartRefreshLayout.Builder(activity)
                .setAutoRefresh(true)
                .setDefaultPagerQuery(editText)
                .setPagerQuery(new Function<IPager, IPageQuery>() {
                    @Override
                    public IPageQuery apply(IPager input) {
                        return new PagerQueryForEdittext(input, editText);
                    }
                })
                .setPagerLoader(new IPageLoader() {
                    @Override
                    public void onPageLoad(boolean isRefresh, IPageable pageable) {

                    }
                })
                .setRefreshLayout(refreshLayout)
                .build();
        refreshLayout.autoRefresh();
    }
}
