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
            case "guard": return ActionGuard(player, args);
            case "explaner": return ActionExplaner(player, args);
            case "weaponshop": return ActionShop(player, args, "WEAPONMASTER");
            case "armorshop": return ActionShop(player, args, "ARMORMASTER");
            case "itemshop": return ActionShop(player, args, "ITEMMASTER");
            case "specialshop": return ActionShop(player, args, "SPECIALMASTER");
            case "foodshop": return ActionShop(player, args, "FOODMASTER");
            case "blockshop": return ActionShop(player, args, "BLOCKMASTER");
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
                case "FOODMASTER": name = "ﾌｰﾄﾞﾏｽﾀｰ"; break;
                case "BLOCKMASTER": name = "ﾌﾞﾛｯｸﾏｽﾀｰ"; break;
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
            npc.getTrait(SendTextTrait.class).setText("よお！ @t！ イイモノ揃ってるぜ！ 俺を右ｸﾘｯｸしてみな！");
            npc.getTrait(SendTextTrait.class).setText("よお！ @t！ お前、そんなので旅してるのか？備えは大切だぜ！");
            // skin
            npc.addTrait(SkinTrait.class);
            switch (kbn) {
                case "WEAPONMASTER":
                    skin_name = "weaponmaster";
                    skin_data = "eyJ0aW1lc3RhbXAiOjE1ODA2ODc2NTU1ODAsInByb2ZpbGVJZCI6IjU2Njc1YjIyMzJmMDRlZTA4OTE3OWU5YzkyMDZjZmU4IiwicHJvZmlsZU5hbWUiOiJUaGVJbmRyYSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTdhYzBjM2U1ZDMwNDcwOWI2ODBjZjRkMjEyZDhjZDQ1MjgxMDc4MzNkYjNhZjA5MDM2MDBjYzFmNzZkZDhkZCJ9fX0=";
                    skin_signature = "SOYZhpJMkVcuyc9IeqXuAORO9iWQUVUy0rn2WMNro+buJ8QE0bA2tCUGHTHKNfRusCLmvYxCTWfAOP/2qr5UPL7prZbmrMUAOGJ8f6PxQ9GXqabIBXNGnf3LwLbEc7k9qkqWY1JOsfJPFOvnXNDxvc01tt61MMbMLPbcvyNqoNNMIuAN5e4o45pmQu02inNnj21OJUYQjqRYMl2kIp0Gk4O2J9xBITRp8P6I9d2Ap9WkBYCc+lCE80ymTjJs3aJoGYFNZiWnGJ2yrvcYxOIPK54NszQYT8qZqn13Sg1mtIyqVwlZ+EUy0Q8R2WH4rL7XWAWWqsj1A08J7cSXtLZmUOFdIZMk/y3fEIB1cVR7qccYRbYmmXP8fv4mJBEqoIjUh3Xqs0NxxI3ZExtbg1O54ZT6bPngLLGl0yR5b4YHrenafDSL8Xw2qQ4oMS22mSerDlpskgEeJOecu9y0kXlGqByLk2cURmh64hJ/CmJrxjJuOl1x+plyYfuVoestebBVpMRUI1HGcCQUSJm9Kuz6zmskVS+/LAjMzjidmwAQmnoxKkS34/6asx2d9g+wIlEZTzZxe0tFv44aCMK3EOUleHFLgK8krNC2EZIa+Vwskq1KUtRb5MQeZjJjruCFgnhkI5mIdEvDNO+SLxxMb8ajIXsQXeZAki2N4OJQ40IE4b4=";
                    break;
                case "ARMORMASTER":
                    skin_name = "armormaster";
                    skin_data = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDc2MTI1ODc1NSwKICAicHJvZmlsZUlkIiA6ICI3MzgyZGRmYmU0ODU0NTVjODI1ZjkwMGY4OGZkMzJmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJ4cWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmMxYzljNmYwNTkzZDdlYzc3OWM1YWZjYmQ3YmM0ZTg3NWFjNjQzYzhiMGU5NjJjZTFkYWJlNzQ3N2I0ZWI0NCIKICAgIH0KICB9Cn0=";
                    skin_signature = "tJO7sPCLQ4DpY9R8kt1Dvhbw86VoNgWiPUEcDAiYpqR2DrlqvUB+5rtQlzQYEm77hpHoJ72lmKDc0C0L4EP1bJtWl8o/IztTHrgOCCL5FL8sb5LrHcDit4p/jKqrSVBf30YhHdIidtCGLhKZFXAZxEWxjaJS6v8i4iFsluKKpUkLxVUBWIHJUpbDZ2uG7Jw4AmQXRuh+0L201aoDI39ap6xK0+w2WA1DQAW/gT+A266s8Q53F136TuKakoH+OYsC/OiLb0CBJuBLi3geMfkZ/jLi4uK9l0v8UUlRK6sDRze+3ZwL7fbTy+qQX76mnAuvHzTHF0IgxcrQCngsn/RCDRDuejhF8bcoULtoUlDGsAsWXirI7JPn+u25DiiGWd0vfkXh6woZNmc1AAOSYAQY8tIUw/27xmi/MtLe2LsvAQRtUCoBS3dvYfWOvWX14Bma1wlQFUkeE8GcxrRue8CjQ+y38pzQ/Qu7vkZnH/X4nMzVFf8W2JRMnxEpLj7ByKQy8GlNrj9H3vzgTAdsY7GNBLSWSDHssN778zTjVwbo8ySXXW/5FmYD4jgm1J2vRUVgoddtAevui64OHIeq8PwKbD22QZ8CzVm20NjEFz/9mg6Sr+uDMxCDARZ/RyQ64qnlBt97zN276PHHulrdiFa0LwT3mRNyNJbpL9CUc/RLjoY=";
                    break;
                case "ITEMMASTER":
                    skin_name = "itemmaster";
                    skin_data = "eyJ0aW1lc3RhbXAiOjE1NjkzMTEwMTU4MTgsInByb2ZpbGVJZCI6Ijc1MTQ0NDgxOTFlNjQ1NDY4Yzk3MzlhNmUzOTU3YmViIiwicHJvZmlsZU5hbWUiOiJUaGFua3NNb2phbmciLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzYzZGNjYmY2MzAxYjA0NGUwMWM1NmIxMDY5NTJmZGNhMTM4MTg1ZjBhNWJiMTE0NDA1ODgyOWRiNzJiMGZhZGMifX19";
                    skin_signature = "MXwoxcQrlIJofZSmmS590rq+6c0YtgVuPH91ndZxrG8dJ0y2AMKrUb48YWVnm0TMZUtmu98MSRxjST/Gl4l2wLwhSMUpt/J7ZIfIN9qSSssyfnIHNc09HrkD1QE/nlKcb0eBLlOm1dZV4YH17ahsPetBWMbBrp+x1dPQ8rH0K7Z9xyUy1MqkVzOZ4eFwjPoCyI2HrhmRmvrsnZFk6VYOu4to10atdASnFYmPOfDIotD6gagpzr7bKezArzweJ+tbOTW1gQRQ/aX4c7W1v58kAiHyRC42gx5HGdJCgfdTS6SjczfxMNKAoH0PN1p1FQHvatlY05wcWWGZD9CWzQYfFnW0KUA4fR6kvxS5ENPI3P4kb6n1D+60eDF5+VxBxkRDWRWvUa48X9CG4YZu5itoJmvt4KjM4d8ZBXXqQtUpb4WdI57Je5LwzW6a8cBDiGOOyCtkp8pDau4396cB+ZjB4oqV+zlLRLokFP2ONbXfjCYZILmrsWt+qURImpWBivA0ltKurcteav3KzXoMZZCiGahpkXMxCK8NSUUVUwJZDSsWVZNAm4CIyB4iIFk5F4T5WbaiLJI4X2S68Iz13/LeSGfI3u3wVdML3YCXghWNZaEPxn//EFiQG9jC868IsILwWBLbO6LmfsbNSRKN3jeY5xvB+3M1VnIr7OfG13pRM68=";
                    break;
                case "SPECIALMASTER":
                    skin_name = "specialmaster";
                    skin_data = "eyJ0aW1lc3RhbXAiOjE1Nzg2NzM2NTczNzIsInByb2ZpbGVJZCI6ImVkNTNkZDgxNGY5ZDRhM2NiNGViNjUxZGNiYTc3ZTY2IiwicHJvZmlsZU5hbWUiOiJGb3J5eExPTCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2MxZGJhMTgzMzMxYmEyMTc2ODkxYjg3OTc3NzliYzQzMjNkMGIzZDdiNGIwNzU4OTAyMTgxMDE5YmY1MGYyZSJ9fX0=";
                    skin_signature = "srocU4SDY0WcfnYrsDkJ/n/8NFNPUyqXr5Lrr9vnybKyN2KUNXztL1bOdaGSLaEBeEWwe7leZnXt6RgS4MmWIy9EhWf80h++U0rbHuuJc6cV/IKBVKD6ruc4C0tJ8R8fi9/pkTRhmTP4vDaAKPauYd0f6wZ49eu25RJnCYL11tS56W/2+2rhkGZFUNp7nCOkertuDWA3Q/6nw/D9+A6wgYHLWIdZY/lmv4Ds3Cm1LM/mNxXFnyF7YysYtA7gOQGV2N65zinOr1Yb+8PpxP46YFAtHSG84F+McqnzaHGlxlH0CIrZ3gPU+B6+nmN76XmWMaMgenYH3EEHw/oHPKJv85SqAmXRPw+8W6EA2Jr9OIS4S8bGrjkhdFhcAlJ9pi3uyxxK2YuvV3xS5yCPHAHeC0Ul3LkoK9QsgZ6Y6g8YFAcWZbvyZsjhw+tobSRLZ2mz+GUEgz2lD5DolRp0q/NFn5tUeaeCSggF7ty1AKc6sxjCaSUk6Ije78+zD8FiNM2mRki93Wa3iHiexPakOOapU81pfYuUFgNfYR7eIQndAFnyj2HfXgY9i6lcSyc+68R6346bDiVPXBAKXIwbpkZmi9lIF5U/dIglpmYxItRkbLrEACN5lVK8leX9zHldnRG8pXyCriQVJkMqugJM5NNyuPxwgwAHe7JS0jNCdvwz7vI=";
                    break;
                case "FOODMASTER":
                    skin_name = "foodmaster";
                    skin_data = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDg1MTMyODI3MywKICAicHJvZmlsZUlkIiA6ICJiMGQ3MzJmZTAwZjc0MDdlOWU3Zjc0NjMwMWNkOThjYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPUHBscyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lODljY2E0NWM2YTllYjZkMWYwMjU1MTZhYjEzMmE5Y2MwOGY0MDE0MWJkZjY1MGUzNTdiZmE2ZWY0ODU1MTQ2IgogICAgfQogIH0KfQ==";
                    skin_signature = "O/a09kBaOvHn4mpRBq2EsxbuROjprbugW2Katfqtua24sG+SEprCkiJCWwzJf2cfNid6u6czdqqRk9sxcY7vWj2wHxVJUU0Mt6WtW2pcXK2a1f1D8zBK1ta10XxIlfBkwTKAyheDvfPhyTI3ieZsQaVnMX6oZ+awz1kBw/ljUlQ698ZQG61mQBm/lJoI9bIwLcdEWO78dGc+xRsRyAyVpob4pWTod6PPXD2YZgiZtjvMzE2o7cYPvYFECeo/XOKRUzWZOpRhtQghkQKaDCzdH1WIpkFbYc+GB2houkSJS0R8209PI86kMkBBGYd73mRmyP69Zj2OLm0Q4bNA9NWC9r5HahkLRTMMSFa2O8vAMMnP0B8JuF/F+R6IGHvpbQjKyj/BPKJJT5KNeG3psIK6kIZ3RRt2CPIQUaEp107cJyYqM3uC6CvUKf1cp93MF7BP8s/anTAxJmar8+iSGdvPQEiDZIAN+jwIzfbuMcmXwcfeGMMprQ6Xo8JUSosmoCDRp4SG9LNR/IAXzaTtSUs6QLYOCwIL9yLFqKQ31hZK/eg1/pPt/Gnh2KsVSwUuB0fefjMqg1xTQ4oDJBOpp8RmHfyw1kQGxeG2FZIZ7sszBlclPz3hrd6qUl/L0xHcH7r+DdP2ROVTB61ATT23S8LRo1usz19GPiEJ2fqfszTO7bs=";
                    break;
                case "BLOCKMASTER":
                    skin_name = "blockmaster";
                    skin_data = "ewogICJ0aW1lc3RhbXAiIDogMTU4ODI2MjU3MzMwNCwKICAicHJvZmlsZUlkIiA6ICJlNzkzYjJjYTdhMmY0MTI2YTA5ODA5MmQ3Yzk5NDE3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVfSG9zdGVyX01hbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kMjAwYWU2ZWU3ZDI1NTRiYjRkMTZiYmNiZTYyZTIzYTM0ZmQ2MWRiMDg2YjliOWIyYzE1NzY5YjZlMzY1Y2Y2IgogICAgfQogIH0KfQ==";
                    skin_signature = "VWU5kl2E3tRpymq9iN+o8xTP/44J/GedocJ8JnN84qGepf5o7Tp3wPUgcsGgF+f2+GUyMXAHzR3ne8IpO/tflAIIt4FEVzMBL31b9NB5PZiKsooDPZ8M+RPojQW13xSsbToaOiEnOn4vDFIKorMh7eBAm6kSgHTLlJ9OWh+cp7LeMbgzxKAVYbU0IlNKby5Na8MqX+lBDxh7VZMBFTLQGQqTQRSZwhsJ7sfNjlWX3X9UjaxJ2IrOCDKWFy7IK7Oi6d5XiymgYOg2vD5+i2NySZ4+iANw9WIrQTU0roT6INqD8hCEmvnO5TZ1uzeq5dDoXcr1xvByJEBzoMMLwYyY7VBUtDQhoXqSuh/9f0cLjKHOHtEqAAVUVMOLrr7RZk501eb4UCXmZ3KxwoezMh9yZTJO6N6ixIzsLdSjbu7ywVvcjKNShspvVepaB30aT+pebRNpJ3p2xKMn0WjxZJdq6k7t8DtSt4wPQ6OXS8KCT1+CiJkRV460LiLqMAmwKky8lDMVWE0funilR5lCA9RPYERBdF/5m1sulrM3dnQVBns45dW/zUtSngQ34zePUeH/Vt730PylqsV48UuN7gWg06X5TArPr+oSogRsafipj/p43DvqnHCPenAJF0Sx88ngUWAGJxkAPUfkYSBJWC/T3vNgcKUX1UIAb6y+V/0cyzM=";
                    break;
                default:
                    skin_name = "master";
                    skin_data = "eyJ0aW1lc3RhbXAiOjE1NjMxMTE1MTcxODcsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVlNDY1YzA3YTgyMjI0ODE5MjYxMjE5N2YyNzcxZTcxZDMzMjYzNDViZDc3OTA3MzEzOTllYWUxM2Y2OWNhOCJ9fX0=";
                    skin_signature = "YFbFMaC5/02UGEHV1wYU2Eo6/Cz4ybnAnIjyAbU5tp13G3Nl/X11j52njzNL82O+YrwldxRL7HEn3/K9+VPp9zY7KTg+Hzh2a4ps4AxwDoXUkqmyhVm5r2UDtZkckPJ+pd08KzIzWjM1/CDhCZ2fLPL0MOeeYNt1IO5uR1aEJe2b/46nhKzArwZ3p/vl5lgN1atfETsLnK9Xi6nTdck8J9jzsqvNJpDek87Y1/p6QFPu9gSWVN15tfv/0DbURvK++0CBx0OR93O/ftsS90KpM08fmfjxWde7dTATKmLcnbJY0QpHZyQ3ohe61uHW4cyB1kU0gS3mbJ/eTrMNKHWUYkAh2us+CDkUXbx6oau/GkmR0LyjRER/wyEczDfVbIMvMqE+h0HkdZSblCzkJsIOYBmapom8G7uDT88bKtQZAWzgPpoNyI8BZmImTA9J5YbudUaLnkN8RVANoED1juG4ilAJO6sXHpeURVnoDbkPXyRVo+8gB/2cHGugLSgjZqCt78KMHbo0yFefNPjeQYMSRfXm9IFLNCANK8rfv3K1Sck/Jwc6LaqGuDl7UZZXKEvIjL/B2hc1FUvcC41MXvEyIFB5Fycm0fqClhR9kdlMfl7NLL/W8dA8mooZlrk/CcC4OlGRKFuM+71jOPmU6sSJGWzkUk2qvTcIlSSEYTCF9yU=";
                    break;
            }
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

    private boolean ActionGuard(Player player, String[] args) {
        try {
            String skin_name = "";
            String skin_data = "";
            String skin_signature = "";

            // create
            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "[NPC] 門番");

            // trait
            Location loc = player.getLocation();

            // trait
            // type
            npc.addTrait(BaseTypeTrait.class);
            npc.getTrait(BaseTypeTrait.class).setType("GUARD");
            // look close
            npc.addTrait(LookClose.class);
            npc.getTrait(LookClose.class).lookClose(true);
            // send text
            npc.addTrait(SendTextTrait.class);
            npc.getTrait(SendTextTrait.class).setCool(30000);
            npc.getTrait(SendTextTrait.class).setRange(8);
            npc.getTrait(SendTextTrait.class).setTextPercent(50);
            npc.getTrait(SendTextTrait.class).setText("近隣のﾓﾝｽﾀｰ？ 大丈夫、俺が蹴散らしてるさ！");
            npc.getTrait(SendTextTrait.class).setText("ｷｬﾝﾀｰﾏ王子が失踪したらしいな");
            npc.getTrait(SendTextTrait.class).setText("雑魚しかいないし門番の必要あるのかな・・・");
            npc.getTrait(SendTextTrait.class).setText("ここを衛るのが俺たちの仕事だよ");
            npc.getTrait(SendTextTrait.class).setText("あんた、そんな恰好で外出るのかい？気を付けなよ！");
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
            npc.getTrait(SendTextTrait.class).setText("土地が買えるみたい。たしか/areaｺﾏﾝﾄﾞだったと思うよ((๑￫ܫ￩๑))");
            npc.getTrait(SendTextTrait.class).setText("自分の土地の中では/homeでﾜｰﾌﾟ設定ができるみたいよ((๑￫ܫ￩๑))");
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
            npc.getTrait(SendTextTrait.class).setText("知ってた？/shopよりNPCの方が取引効率いいのよ♥♥(o￫ܫ￩o)♫");
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
