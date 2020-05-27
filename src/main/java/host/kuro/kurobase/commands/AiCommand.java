package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.trait.BaseTypeTrait;
import host.kuro.kurobase.trait.BuddyMasterTrait;
import host.kuro.kurobase.trait.ExplanerTrait;
import host.kuro.kurobase.trait.SendTextTrait;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.ParticleUtils;
import host.kuro.kurobase.utils.SoundUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AiCommand implements CommandExecutor {

    private KuroBase plugin;
    public AiCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        Player player = (Player)sender;
        if (args.length != 1) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        switch(args[0].toLowerCase()) {
            case "buddymaster": return ActionBuddyMaster(player, args);
            case "explaner": return ActionExplaner(player, args);
            case "weaponshop": return ActionShop(player, args, "WEAPONSMASTER");
            case "armorshop": return ActionShop(player, args, "ARMORMASTER");
            case "itemshop": return ActionShop(player, args, "ITEMMASTER");
            case "specialshop": return ActionShop(player, args, "SPECIALMASTER");
        }
        return true;
    }

    private boolean ActionShop(Player player, String[] args, String kbn) {
        try {
            String skin_name = "";
            String skin_data = "";
            String skin_signature = "";

            String name = "";
            switch (kbn) {
                case "WEAPONMASTER": name = "ｳｪﾎﾟﾝﾏｽﾀｰ"; break;
                case "ARMORMASTER": name = "ｱｰﾏｰﾏｽﾀｰ"; break;
                case "ITEMMASTER": name = "ｱｲﾃﾑﾏｽﾀｰ"; break;
                case "SPECIALMASTER": name = "ｽﾍﾟｼｬﾙﾏｽﾀｰ"; break;
            }

            // create
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "[NPC] "+name);

            // trait
            Location loc = player.getLocation();

            // trait
            // type
            npc.addTrait(BaseTypeTrait.class);
            npc.getTrait(BaseTypeTrait.class).setType(kbn);
            // look close
            npc.addTrait(LookClose.class);
            npc.getTrait(LookClose.class).lookClose(true);
            // send text
            npc.addTrait(SendTextTrait.class);
            npc.getTrait(SendTextTrait.class).setCool(30000);
            npc.getTrait(SendTextTrait.class).setRange(8);
            npc.getTrait(SendTextTrait.class).setTextPercent(50);
            npc.getTrait(SendTextTrait.class).setText("よお！ @t！ 取引しないか？ 俺を右ｸﾘｯｸしてみな！");
            // skin
            npc.addTrait(SkinTrait.class);
            skin_name = "master";
            skin_data = "eyJ0aW1lc3RhbXAiOjE1NjMxMTE1MTcxODcsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVlNDY1YzA3YTgyMjI0ODE5MjYxMjE5N2YyNzcxZTcxZDMzMjYzNDViZDc3OTA3MzEzOTllYWUxM2Y2OWNhOCJ9fX0=";
            skin_signature = "YFbFMaC5/02UGEHV1wYU2Eo6/Cz4ybnAnIjyAbU5tp13G3Nl/X11j52njzNL82O+YrwldxRL7HEn3/K9+VPp9zY7KTg+Hzh2a4ps4AxwDoXUkqmyhVm5r2UDtZkckPJ+pd08KzIzWjM1/CDhCZ2fLPL0MOeeYNt1IO5uR1aEJe2b/46nhKzArwZ3p/vl5lgN1atfETsLnK9Xi6nTdck8J9jzsqvNJpDek87Y1/p6QFPu9gSWVN15tfv/0DbURvK++0CBx0OR93O/ftsS90KpM08fmfjxWde7dTATKmLcnbJY0QpHZyQ3ohe61uHW4cyB1kU0gS3mbJ/eTrMNKHWUYkAh2us+CDkUXbx6oau/GkmR0LyjRER/wyEczDfVbIMvMqE+h0HkdZSblCzkJsIOYBmapom8G7uDT88bKtQZAWzgPpoNyI8BZmImTA9J5YbudUaLnkN8RVANoED1juG4ilAJO6sXHpeURVnoDbkPXyRVo+8gB/2cHGugLSgjZqCt78KMHbo0yFefNPjeQYMSRfXm9IFLNCANK8rfv3K1Sck/Jwc6LaqGuDl7UZZXKEvIjL/B2hc1FUvcC41MXvEyIFB5Fycm0fqClhR9kdlMfl7NLL/W8dA8mooZlrk/CcC4OlGRKFuM+71jOPmU6sSJGWzkUk2qvTcIlSSEYTCF9yU=";
            npc.getTrait(SkinTrait.class).setSkinPersistent(skin_name, skin_signature, skin_data);
            // equipent
            //ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
            //sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
            //npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, sword);
            // buddy master
            npc.addTrait(BuddyMasterTrait.class);
            npc.getTrait(BuddyMasterTrait.class).setLocation(loc);
            // spawn setting
            npc.setFlyable(false);
            npc.setProtected(true);
            npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, true);
            npc.data().setPersistent(NPC.DAMAGE_OTHERS_METADATA, true);
            npc.spawn(loc);

            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.DRIP_LAVA, 50); // particle
            SoundUtils.BroadcastSound("typewriter-2", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionBuddyMaster(Player player, String[] args) {
        try {
            String skin_name = "";
            String skin_data = "";
            String skin_signature = "";

            // create
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "[NPC] ﾊﾞﾃﾞｨｰﾏｽﾀｰ");

            // trait
            Location loc = player.getLocation();

            // trait
            // type
            npc.addTrait(BaseTypeTrait.class);
            npc.getTrait(BaseTypeTrait.class).setType("BUDDYMASTER");
            // look close
            npc.addTrait(LookClose.class);
            npc.getTrait(LookClose.class).lookClose(true);
            // send text
            npc.addTrait(SendTextTrait.class);
            npc.getTrait(SendTextTrait.class).setCool(30000);
            npc.getTrait(SendTextTrait.class).setRange(8);
            npc.getTrait(SendTextTrait.class).setTextPercent(50);
            npc.getTrait(SendTextTrait.class).setText("近隣のﾓﾝｽﾀｰ？ 大丈夫、俺が蹴散らしてるさ！");
            npc.getTrait(SendTextTrait.class).setText("よお！ @t！ ﾊﾞﾃﾞｨｰ書物の取引をしないか？ 俺を右ｸﾘｯｸしてみな！");
            npc.getTrait(SendTextTrait.class).setText("お前のﾊﾞﾃﾞｨｰは育ってるか？気長に育てないとな！");
            npc.getTrait(SendTextTrait.class).setText("ﾊﾞﾃﾞｨｰが死んだら、復活の所を買うしかない！俺を右ｸﾘｯｸしてみな！");
            npc.getTrait(SendTextTrait.class).setText("ﾊﾞﾃﾞｨｰが欲しいのかい？なら書物の取引をしないか？俺を右ｸﾘｯｸしてみな！");
            // skin
            npc.addTrait(SkinTrait.class);
            skin_name = "master";
            skin_data = "eyJ0aW1lc3RhbXAiOjE1NjMxMTE1MTcxODcsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVlNDY1YzA3YTgyMjI0ODE5MjYxMjE5N2YyNzcxZTcxZDMzMjYzNDViZDc3OTA3MzEzOTllYWUxM2Y2OWNhOCJ9fX0=";
            skin_signature = "YFbFMaC5/02UGEHV1wYU2Eo6/Cz4ybnAnIjyAbU5tp13G3Nl/X11j52njzNL82O+YrwldxRL7HEn3/K9+VPp9zY7KTg+Hzh2a4ps4AxwDoXUkqmyhVm5r2UDtZkckPJ+pd08KzIzWjM1/CDhCZ2fLPL0MOeeYNt1IO5uR1aEJe2b/46nhKzArwZ3p/vl5lgN1atfETsLnK9Xi6nTdck8J9jzsqvNJpDek87Y1/p6QFPu9gSWVN15tfv/0DbURvK++0CBx0OR93O/ftsS90KpM08fmfjxWde7dTATKmLcnbJY0QpHZyQ3ohe61uHW4cyB1kU0gS3mbJ/eTrMNKHWUYkAh2us+CDkUXbx6oau/GkmR0LyjRER/wyEczDfVbIMvMqE+h0HkdZSblCzkJsIOYBmapom8G7uDT88bKtQZAWzgPpoNyI8BZmImTA9J5YbudUaLnkN8RVANoED1juG4ilAJO6sXHpeURVnoDbkPXyRVo+8gB/2cHGugLSgjZqCt78KMHbo0yFefNPjeQYMSRfXm9IFLNCANK8rfv3K1Sck/Jwc6LaqGuDl7UZZXKEvIjL/B2hc1FUvcC41MXvEyIFB5Fycm0fqClhR9kdlMfl7NLL/W8dA8mooZlrk/CcC4OlGRKFuM+71jOPmU6sSJGWzkUk2qvTcIlSSEYTCF9yU=";
            npc.getTrait(SkinTrait.class).setSkinPersistent(skin_name, skin_signature, skin_data);
            // equipent
            ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
            sword.addEnchantment(Enchantment.DAMAGE_ALL, 5);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, sword);
            // buddy master
            npc.addTrait(BuddyMasterTrait.class);
            npc.getTrait(BuddyMasterTrait.class).setLocation(loc);
            // spawn setting
            npc.setFlyable(false);
            npc.setProtected(true);
            npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, true);
            npc.data().setPersistent(NPC.DAMAGE_OTHERS_METADATA, true);
            npc.spawn(loc);

            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.DRIP_LAVA, 50); // particle
            SoundUtils.BroadcastSound("typewriter-2", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionExplaner(Player player, String[] args) {
        try {
            String skin_name = "";
            String skin_data = "";
            String skin_signature = "";

            // create
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "[NPC] ｵｼｴﾃﾁｬﾝ");

            // trait
            Location loc = player.getLocation();

            // trait
            // type
            npc.addTrait(BaseTypeTrait.class);
            npc.getTrait(BaseTypeTrait.class).setType("EXPLANER");
            // look close
            npc.addTrait(LookClose.class);
            npc.getTrait(LookClose.class).lookClose(true);
            // send text
            npc.addTrait(SendTextTrait.class);
            npc.getTrait(SendTextTrait.class).setCool(60000);
            npc.getTrait(SendTextTrait.class).setRange(10);
            npc.getTrait(SendTextTrait.class).setTextPercent(50);
            npc.getTrait(SendTextTrait.class).setText("123鯖は2020年5月から導入開始した、まだ誕生したての鯖なのよ٩( 'ω' )و");
            npc.getTrait(SendTextTrait.class).setText("統合版では約５年間くらいの実績がある鯖なんだって((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("まだまだ鯖は発展途上ね٩( 'ω' )و");
            npc.getTrait(SendTextTrait.class).setText("ｻﾊﾞｲﾊﾞﾙとｸﾘｴｲﾃｨﾌﾞの両ﾜｰﾙﾄﾞがあるって知ってた？((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("worldはｻﾊﾞｲﾊﾞﾙｴﾘｱね、cityはｸﾘｴｲﾃｨﾌﾞｴﾘｱみたいよ((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("ｻﾊﾞｲﾊﾞﾙｴﾘｱでは土地が買えるみたい。たしか/areaｺﾏﾝﾄﾞだったと思うよ((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("ｻﾊﾞｲﾊﾞﾙｴﾘｱの自分の土地の中では/homeでﾜｰﾌﾟ設定ができるみたいよ((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("ｺﾏﾝﾄﾞはWEBで見るほうがｲｲﾖ http://kuro.host/ ((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("ここに住んでいる住民のﾘｽﾄはWEBで見れるしｽﾃｰﾀｽも見れるみたい http://kuro.host/ ((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("細かい説明はWEBがｲﾁﾊﾞﾝね！ http://kuro.host/ ((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("今自分がいる位置がわからない時は/map！URLをｸﾘｯｸするとページが見れるよ((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("いらないものを売ったり、欲しい物を買いたい時ってあるよね？そんな時は/shop((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("なんか鯖主がさー！動画とか写真とかｱｯﾌﾟしてるんだけど視聴回数少なすぎない？ウケる！（*థ౪థ）");
            npc.getTrait(SendTextTrait.class).setText("WEBでﾗﾝｷﾝｸﾞが見れるんだけどｺﾝﾃﾝﾂがまだまだ少ないね、鯖主サボり気味なのかな((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("ﾙｰﾙはWEBの「123鯖について」で確認しようね、ﾌﾟﾚｲしている時点で同意してるよ((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("ﾌﾟﾚｲﾔｰﾘｽﾄを見たい時はﾀｳｷｰの長押しか/listよ♥♥(o￫ܫ￩o)♫");
            npc.getTrait(SendTextTrait.class).setText("最初の1時間ﾌﾟﾚｲは権限が「見習」だけど超えると自動で「住民」になるから安心しなよ♥♥(o￫ܫ￩o)♫");
            npc.getTrait(SendTextTrait.class).setText("ｸﾘｯｸしてﾌﾞﾛｯｸの情報を見たい時は/biよ♥♥(o￫ܫ￩o)♫");
            npc.getTrait(SendTextTrait.class).setText("ﾁｪｽﾄをﾛｯｸしたい時は/chestよ♥♥(o￫ܫ￩o)♫");
            npc.getTrait(SendTextTrait.class).setText("cityでは住民なら/cでｸﾘｴになれるよ！建築の手伝いも楽しいと思うよ♥♥(o￫ܫ￩o)♫");
            npc.getTrait(SendTextTrait.class).setText("誰かにお金を払いたい時は/payよ♥♥(o￫ܫ￩o)♫");
            npc.getTrait(SendTextTrait.class).setText("ﾗﾝﾀﾞﾑな数値出して遊びたい時は/randよ♥♥(o￫ܫ￩o)♫");
            npc.getTrait(SendTextTrait.class).setText("ｲﾝﾍﾞﾝﾄﾘをすっきりﾎﾟｲしたい時は/riよ♥♥(o￫ܫ￩o)♫");
            npc.getTrait(SendTextTrait.class).setText("/sitで座れるって知ってた？♥♥(o￫ܫ￩o)♫");
            npc.getTrait(SendTextTrait.class).setText("cityは今後NPC達が増えたり街ができたりして楽しめる要素が増えていくみたいよ(๑￫ܫ￩)b☆ｲｪｨ♪");
            npc.getTrait(SendTextTrait.class).setText("DISCORDとかTWITTERやｲﾝｽﾀもあるからﾌｫﾛｰしてみたら？鯖主はｼｮﾎﾞいけどね♥(｡￫v￩｡)♥");
            npc.getTrait(SendTextTrait.class).setText("元気ー？( *ᵅั ω ᵅั*)");
            npc.getTrait(SendTextTrait.class).setText("あなたﾎﾞｯﾁ？心配ないよ！あたしがいるからね( *ᵅั ω ᵅั*)");
            // skin
            npc.addTrait(SkinTrait.class);
            skin_name = "pretty";
            skin_data = "eyJ0aW1lc3RhbXAiOjE1NzA2NDIyOTM5NzgsInByb2ZpbGVJZCI6ImZkNjBmMzZmNTg2MTRmMTJiM2NkNDdjMmQ4NTUyOTlhIiwicHJvZmlsZU5hbWUiOiJSZWFkIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81ODg3NzNkYWEzMjgwMTZjYjI5NDBkMjNkODZmNzk2YTgwMWQxNDViNTEwYmU1OGM5YTQ4MWJiYzlkMThiZjMyIn19fQ==";
            skin_signature = "AnZ2Hv4enP36E96N9tDBeAqYYZVQNxrMGFzEtspsEsK0+m7ffn96NvdAc45RT5v3nb4BBX5XnzyMYh0HhLmqczQzJwcga6d2iG89eSMn+f0VlW6wOADB/6kVeoP1Jj7KdIVXeIvlR+x2dFOfl84kelN/8qOpGlZCV1ZIlYFRM19PQg5iS2fsGFmcuKiUByFdtfgErtePBru8tZRLUG8wNhZ2PMhtnP4o7JYiBgIwVjILfQN2DSJhRqb30qx1PbOkiM0WcqtDpl1HGG3+dJMA23nsF+2c3BYsOYoSXnrmAs0MackmKE6BsQqZ/t7A/tfnR79NrlOeSqK41KRTaj6wzz47IpWmyx+6z/gzGJe5GVZljXJ+08+9lbOuGA006NlsPcQGxMGcdZejedah9O4K5KjNbjCGKsYH7KSWhXIqeqDJfSanIPYe2sKJ1nUJ+M0VCINa9IN/f3LhMptDFdD9tr8jjYutpeabTEYTmiKd7cXjyT4NKxCFKlrHQBjNw2U9UuvD+KmDd64JGft2bDp65l5L+gwE/F7aRvVGWHeC9h/loof5pJ0RNVwItwQqhOJegV8+sngFmHnrND01m0YluCNasREJ9JmTmfqutQybiLU23AD9Ey8MVxL7peEIWFuE6TqtiN0UB+2vRpAiP3mh+sQWdn2ezi35lqRbSOClCsM=";
            npc.getTrait(SkinTrait.class).setSkinPersistent(skin_name, skin_signature, skin_data);
            // explaner
            npc.addTrait(ExplanerTrait.class);
            // spawn setting
            npc.setFlyable(false);
            npc.setProtected(true);
            npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, true);
            npc.data().setPersistent(NPC.DAMAGE_OTHERS_METADATA, false);
            npc.spawn(loc);

            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.DRIP_LAVA, 50); // particle
            SoundUtils.BroadcastSound("typewriter-2", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }
}
