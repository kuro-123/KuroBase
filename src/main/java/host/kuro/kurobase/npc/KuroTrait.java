package host.kuro.kurobase.npc;

import host.kuro.kurobase.KuroBase;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import org.bukkit.event.EventHandler;

public class KuroTrait extends Trait {

    public KuroTrait() {
        super("KuroTrait");
    }

    boolean SomeSetting = false;

    // 永続性API
    @Persist("KuroSetting") boolean automaticallyPersistedSetting = false;

    // 以前に保存した値をロードする必要があります（オプション）
    // 特性を初めて適用するときに呼び出されず、サーバーの起動時に既存のnpcにのみロードされます
    // AFTER onAttachと呼ばれるため、onAttachでデフォルトをロードすると、ここでオーバーライドされます
    // onSpawnの前に呼び出され、npc.getBukkitEntity（）はnullを返します。
    public void load(DataKey key) {
        SomeSetting = key.getBoolean("SomeSetting", false);
    }

    // NPCの設定を保存します（オプション）。これらの値はNPCの保存ファイルに保持されます
    public void save(DataKey key) {
        key.setBoolean("SomeSetting",SomeSetting);
    }

    // イベントハンドラの例。すべてのトレイトはBukkitリスナーとして自動的に登録されます
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event){
        //Handle a click on a NPC. The event has a getNPC() method.
        //Be sure to check event.getNPC() == this.getNPC() so you only handle clicks on this NPC!

    }

    // Called every tick
    @Override
    public void run() {
    }

    //トレイトがNPCにアタッチされているときにコードを実行します
    //これはonSpawnの前に呼び出されるため、npc.getBukkitEntity（）はnullを返します
    //これは、新しいNPCの構成可能なデフォルトをロードするのに適した場所です
    @Override
    public void onAttach() {
        KuroBase.GetInstance().getServer().getLogger().info(npc.getName() + "has been assigned KuroTrait!");
    }

    //NPCが生成されたときにコードを実行します。これは、エンティティが実際にスポーンする前に呼び出されるため、npc.getBukkitEntity（）は引き続き有効です
    @Override
    public void onDespawn() {
    }

    //NPCが生成されたときにコードを実行します。このメソッドが呼び出されるまで、npc.getBukkitEntity（）はnullになることに注意してください
    //これは、サーバー起動時のAFTER onAttachおよびAFTER Loadと呼ばれます
    @Override
    public void onSpawn() {

    }

    //NPCが削除されたときにコードを実行します。これを使用して、繰り返されるタスクをすべて破棄します
    @Override
    public void onRemove() {
    }
}
