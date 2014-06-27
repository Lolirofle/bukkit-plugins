package tk.flygande_toalett.minecraft.usertools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class ItemUtil{
	private ItemUtil(){}
	
	public static HashMap<Material,Integer> materialCount(Collection<ItemStack> itemStacks){
		HashMap<Material,Integer> result = new HashMap<Material,Integer>();
		
		for(ItemStack stack : itemStacks){
			if(stack==null)
				continue;
			
			Integer amount = result.get(stack.getType());
			if(amount==null)
				amount=0;
			amount+=stack.getAmount();
			result.put(stack.getType(),amount);
		}
		
		return result;
	}
	
	public static List<Recipe> getRecipesUsing(Collection<ItemStack> items,Server server){
		//Result recipes
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		
		//Combine duplicate item types into item counts
		HashMap<Material,Integer> requiredItems = materialCount(items);
		
		RecipeLoop: for(Iterator<Recipe> i=server.recipeIterator();i.hasNext();){
			final Recipe recipe = i.next();
			
			if(recipe instanceof FurnaceRecipe){
				//Furnace recipes are only able to have one ingredient 
				if(requiredItems.size()==1){
					final FurnaceRecipe furnaceRecipe = (FurnaceRecipe)recipe;
					
					//Check if the items specified are enough for making recipe
					Integer requiredItemAmount = requiredItems.get(furnaceRecipe.getInput().getType());
					if(requiredItemAmount!=null && furnaceRecipe.getInput().getAmount()<=requiredItemAmount)
						recipes.add(recipe);
				}
			}else if(recipe instanceof ShapelessRecipe){
				final List<ItemStack> ingredients = ((ShapelessRecipe)recipe).getIngredientList();
				
				//Check if the items specified are enough for making recipe, continue check next recipe if not matching
				for(ItemStack ingredient : ingredients){
					Integer requiredItemAmount = requiredItems.get(ingredient.getType());
					if(requiredItemAmount!=null && ingredient.getAmount()<=requiredItemAmount){
						recipes.add(recipe);
						continue RecipeLoop;
					}
				}
			}else if(recipe instanceof ShapedRecipe){
				final HashMap<Material,Integer> ingredients = materialCount(((ShapedRecipe)recipe).getIngredientMap().values());
				
				//Check if the items specified are enough for making recipe, continue check next recipe if not matching
				for(Entry<Material,Integer> ingredient : ingredients.entrySet()){
					Integer requiredItemAmount = requiredItems.get(ingredient.getKey());
					if(requiredItemAmount!=null && ingredient.getValue()<=requiredItemAmount){
						recipes.add(recipe);
						continue RecipeLoop;
					}
				}
			}
		}
		
		return recipes;
	}
}
