package d.i.m.p.l.e;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TIMER_err
 * @link github.com/TIMER-err
 */
public class package_info {//ProtectionLoader
    private static final Map<String, Class<?>> loadedClass = new HashMap<>();
    static boolean init = false;
    /**
     * @link debug
     * &#064;Tip: 构建时改成false
     **/


    private static void load() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        init = true;
        //loadedClass.put("Launch", ProtectedLaunch.class);loadedClass.put("Login", LoginGUI.class);

    }

    public static Map<String, Class<?>> getLoadedClass() {

        return loadedClass;
    }
}