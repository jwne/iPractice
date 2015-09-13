package com.shawckz.ipractice.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ---------- GuruCraft ----------
 * Created by Fraser.Cumming on 06/04/2015.
 * © 2015 Fraser Cumming All Rights Reserved
 */
public class ItemUtil {

    public static String getName( ItemStack item ) {
        Validate.isTrue( item.hasItemMeta() );
        return item.getItemMeta().getDisplayName();
    }

    public static void setName( ItemStack item, String name ) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
    }

    /**
     * Serializes an ItemStack into a String.
     *
     * @param stack ItemStack being converted into a String.
     *
     * @return Serialized String of the ItemStack.
     */
    public static String itemToString( ItemStack stack ) {
        StringBuilder builder = new StringBuilder( "" );

        builder.append( stack.getType().name() ).append( "," );
        builder.append( stack.getDurability() ).append( "," );
        builder.append( stack.getAmount() ).append( ";" );

        if ( stack.getEnchantments().keySet().size() > 0 ) {
            for ( Enchantment enchantment : stack.getEnchantments().keySet() ) {
                int level = stack.getEnchantments().get( enchantment );

                builder.append( enchantment.getName() ).append( "," ).append( level ).append( "&" );
            }
        }

        builder.deleteCharAt( builder.length() - 1 );

        if ( stack.hasItemMeta() ) {
            final String customName = ItemUtil.getName( stack );

            if ( customName != null ) {
                builder.append( ";" );
                builder.append( "-n" );
                builder.append( customName );
            }
        }

        if ( stack.hasItemMeta() && stack.getItemMeta().hasLore() ) {
            builder.append( ";-l" );

            for ( String string : stack.getItemMeta().getLore() ) {
                builder.append( string ).append( "@" );
            }
        }

        if ( stack.getItemMeta() instanceof LeatherArmorMeta ) {
            LeatherArmorMeta meta = (LeatherArmorMeta) stack.getItemMeta();

            if ( meta.getColor() != null ) {
                builder.append( ";-c" ).append( meta.getColor().asRGB() );
            }
        }

        if ( stack.getType() == Material.SKULL_ITEM ) {
            String skullName = ( (SkullMeta) stack.getItemMeta() ).getOwner();

            if ( skullName != null ) {
                builder.append( ";" );
                builder.append( "-s" );
                builder.append( skullName );
            }
        }

        if ( ( stack.getItemMeta() instanceof EnchantmentStorageMeta ) || ( stack.getType() == Material.ENCHANTED_BOOK ) ) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) stack.getItemMeta();

            if ( bookMeta != null ) {
                Map<Enchantment, Integer> enchantments = bookMeta.getStoredEnchants();

                if ( !enchantments.isEmpty() ) {
                    boolean found = false;

                    builder.append( ";" );
                    builder.append( "-e" );

                    for ( Enchantment E : enchantments.keySet() ) {
                        int level = enchantments.get( E );

                        builder.append( E.getName() ).append( ":" ).append( level ).append( "&" );
                        found = true;
                    }

                    if ( found ) {
                        builder.deleteCharAt( builder.length() - 1 );
                    }
                }
            }
        }

        if ( ( stack.getItemMeta() instanceof BookMeta ) && stack.getType().equals( Material.WRITTEN_BOOK ) ) {
            BookMeta bookMeta = (BookMeta) stack.getItemMeta();

            if ( bookMeta != null ) {
                String author = bookMeta.getAuthor();
                String title = bookMeta.getTitle();
                List<String> pages = bookMeta.getPages();

                if ( bookMeta.hasAuthor() ) {
                    builder.append( ";" );
                    builder.append( "-a" );
                    builder.append( author );
                }

                if ( bookMeta.hasTitle() ) {
                    builder.append( ";" );
                    builder.append( "-t" );
                    builder.append( title );
                }

                if ( bookMeta.hasPages() ) {
                    builder.append( ";" );
                    builder.append( "-p" );

                    for ( String page : pages ) {
                        builder.append( page ).append( "%" );
                    }
                }
            }
        }

        return builder.toString();
    }

    /**
     * Converts an item String into an ItemStack.
     *
     * @param itemString String to be converted to an ItemStack.
     *
     * @return ItemStack converted from the String.
     */
    public static ItemStack stringToItem( String itemString ) {
        String[] itemDataNEnchants = itemString.split( ";" );
        String[] values = itemDataNEnchants[0].split( "," );
        ItemStack item = new ItemStack( Material.getMaterial( values[0] ), Integer.parseInt( values[2] ),
                                        Short.parseShort( values[1] ) );

        for ( int i = 1; i < itemDataNEnchants.length; i++ ) {
            if ( i > itemDataNEnchants.length ) {
                break;
            }

            String dataString = itemDataNEnchants[i];

            if ( dataString.startsWith( "-" ) ) {
                final char c = dataString.charAt( 1 );

                switch ( c ) {
                    case 'n':
                        ItemUtil.setName( item, dataString.replace( "-n", "" ) );

                        break;

                    case 's':
                        if ( item.getType() == Material.SKULL_ITEM ) {
                            final SkullMeta skullmeta = (SkullMeta) item.getItemMeta();

                            skullmeta.setOwner( dataString.replace( "-s", "" ) );
                            item.setItemMeta( skullmeta );
                        }

                        break;

                    case 'l':
                        List<String> lore = new ArrayList<String>();

                        Collections.addAll( lore, dataString.substring( 2 ).split( "@" ) );

                        ItemMeta itemMeta = item.getItemMeta();

                        itemMeta.setLore( lore );
                        item.setItemMeta( itemMeta );

                        break;

                    case 'c':
                        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

                        meta.setColor( Color.fromRGB( Integer.parseInt( dataString.substring( 2 ) ) ) );
                        item.setItemMeta( meta );

                        break;

                    case 'e':
                        if ( item.getType() == Material.ENCHANTED_BOOK ) {
                            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) item.getItemMeta();

                            for ( String enchantmentString : dataString.substring( 2 ).split( "&" ) ) {
                                String enchantmentName = enchantmentString.split( ":" )[0];
                                int enchantmentLevel = Integer.decode( enchantmentString.split( ":" )[1] );

                                if ( Enchantment.getByName( enchantmentName ) == null ) {
                                    continue;
                                }

                                bookMeta.addStoredEnchant( Enchantment.getByName( enchantmentName ), enchantmentLevel, false );
                            }

                            item.setItemMeta( bookMeta );
                        }

                        break;

                    case 'a':
                        if ( item.getType().equals( Material.WRITTEN_BOOK ) ) {
                            BookMeta bookMeta = (BookMeta) item.getItemMeta();

                            bookMeta.setAuthor( dataString.replace( "-a", "" ) );
                            item.setItemMeta( bookMeta );
                        }

                        break;

                    case 't':
                        if ( item.getType().equals( Material.WRITTEN_BOOK ) ) {
                            BookMeta bookMeta = (BookMeta) item.getItemMeta();

                            bookMeta.setTitle( dataString.replace( "-t", "" ) );
                            item.setItemMeta( bookMeta );
                        }

                        break;

                    case 'p':
                        if ( item.getType().equals( Material.WRITTEN_BOOK ) ) {
                            BookMeta bookMeta = (BookMeta) item.getItemMeta();
                            String[] split = dataString.replace( "-p", "" ).split( "%" );

                            bookMeta.addPage( split );
                            item.setItemMeta( bookMeta );
                        }

                        break;
                }
            } else {

                // check for multiple enchants
                if ( dataString.contains( "&" ) ) {
                    String[] enchants = dataString.split( "&" );

                    for ( String enchantmentString : enchants ) {
                        addEnchantFromString( item, enchantmentString );
                    }

                } else {
                    addEnchantFromString( item, dataString );
                }
            }
        }

        return item;
    }

    /**
     * Add an enchantment string to an ItemStack.
     *
     * @param itemStack         ItemStack to have the enchantment added to it.
     * @param enchantmentString Enchantment String to be added to the ItemStack.
     */
    private static void addEnchantFromString( ItemStack itemStack, String enchantmentString ) {
        String[] enchantmentStringSplit = enchantmentString.split( "," );

        if ( enchantmentStringSplit.length == 2 ) {
            String enchantName = enchantmentStringSplit[0];
            int level = Integer.parseInt( enchantmentStringSplit[1] );

            itemStack.addUnsafeEnchantment( Enchantment.getByName( enchantName ), level );
        }
    }

}
