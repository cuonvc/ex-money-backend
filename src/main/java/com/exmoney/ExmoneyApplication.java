package com.exmoney;

import com.exmoney.entity.ExpenseCategory;
import com.exmoney.repository.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.exmoney.util.Constant.RecordType.DEFAULT;
import static com.exmoney.util.Utils.getNow;

@EnableAsync
@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class ExmoneyApplication implements CommandLineRunner {

	private final ExpenseCategoryRepository categoryRepository;
	private static final LocalDateTime NOW = getNow();

	public static void main(String[] args) {
		SpringApplication.run(ExmoneyApplication.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		log.info("=================> INIT DEFAULT RESOURCE STARTING ...");
//		initDefaultCategory(); //use khi khởi tạo env mới
		log.info("INIT DEFAULT RESOURCE DONE <=================");
	}

	private void initDefaultCategory() {
		//save root category list -> get id
		Set<ExpenseCategory> rootCategories = DEFAULT_CATEGORY_NAME_MAP_IMAGE.entrySet()
				.stream()
				.map(c -> categoryRepository.findDefaultByName(c.getKey())
						.orElseGet(() -> ExpenseCategory.builder()
								.name(c.getKey())
								.iconImage(c.getValue())
								.type(DEFAULT)
								.createdBy(0L)
								.updatedBy(0L)
								.createdAt(NOW)
								.updatedAt(NOW)
								.build()
						)
				).collect(Collectors.toSet());
		categoryRepository.saveAll(rootCategories);

		//save sub categories with parent id
		rootCategories.forEach(root -> DEFAULT_SUB_CATEGORY_NAME_MAP_IMAGE.forEach((name, image) -> {
            if (name.contains(root.getName()) && categoryRepository.findDefaultByName(name).isEmpty()) {
				categoryRepository.save(
						ExpenseCategory.builder()
								.name(name)
								.iconImage(image)
								.type(DEFAULT)
								.createdBy(0L)
								.updatedBy(0L)
								.parentId(root.getId())
								.createdAt(NOW)
								.updatedAt(NOW)
								.build()
				);
            }
		}));
	}

	public static final Map<String, String> DEFAULT_CATEGORY_NAME_MAP_IMAGE = Map.ofEntries(

			Map.entry("default.category.health", "health"),
			Map.entry("default.category.gym", "gym"),
			Map.entry("default.category.shopping", "shopping"),
			Map.entry("default.category.investment", "investment"),
			Map.entry("default.category.sport", "sport"),
			Map.entry("default.category.pets", "pets"),
			Map.entry("default.category.entertainment", "entertainment"),
			Map.entry("default.category.children", "children"),
			Map.entry("default.category.living", "living"),
			Map.entry("default.category.margin", "margin"),
			Map.entry("default.category.food", "food"),
			Map.entry("default.category.personal", "personal"),
			Map.entry("default.category.gift", "gift"),
			Map.entry("default.category.other", "other")
	);

	public static final Map<String, String> DEFAULT_SUB_CATEGORY_NAME_MAP_IMAGE = Map.ofEntries(
			Map.entry("default.category.children_babysitting", "children_babysitting"),
			Map.entry("default.category.children_tuition", "children_tuition"),
			Map.entry("default.category.children_toy", "children_toy"),
			Map.entry("default.category.children_pocketmoney", "children_pocketmoney"),
			Map.entry("default.category.children_milk", "children_milk"),
			Map.entry("default.category.health_pharmacy", "health_pharmacy"),
			Map.entry("default.category.health_insurance", "health_insurance"),
			Map.entry("default.category.health_doctor", "health_doctor"),
			Map.entry("default.category.living_xecongnghe", "living_xecongnghe"),
			Map.entry("default.category.living_phone", "living_phone"),
			Map.entry("default.category.personal_hobbies", "personal_hobbies"),
			Map.entry("default.category.living_servicefix", "living_servicefix"),
			Map.entry("default.category.living_internet", "living_internet"),
			Map.entry("default.category.living_home", "living_home"),
			Map.entry("default.category.living_electricity", "living_electricity"),
			Map.entry("default.category.living_fuel", "living_fuel"),
			Map.entry("default.category.living_gas", "living_gas"),
			Map.entry("default.category.living_water", "living_water"),
			Map.entry("default.category.living_taxi", "living_taxi"),
			Map.entry("default.category.personal_onlineservice", "personal_onlineservice"),
			Map.entry("default.category.personal_education", "personal_education"),
			Map.entry("default.category.food_restaurant", "food_restaurant"),
			Map.entry("default.category.food_cart", "food_cart"),
			Map.entry("default.category.food_dinner", "food_dinner"),
			Map.entry("default.category.food_lunch", "food_lunch"),
			Map.entry("default.category.food_breakfast", "food_breakfast"),
			Map.entry("default.category.food_coffee", "food_coffee"),
			Map.entry("default.category.personal_relax", "personal_relax"),
			Map.entry("default.category.shopping_accessories", "shopping_accessories"),
			Map.entry("default.category.shopping_shoes", "shopping_shoes"),
			Map.entry("default.category.shopping_clothing", "shopping_clothing")
	);
}
