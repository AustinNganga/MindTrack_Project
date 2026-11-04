# Depression score-group modeling

This workflow predicts the dataset-defined 11+ Beck-score group from the agreed background and phone-use inputs plus the recalculated phone-dependence total. The target is a research score grouping, not a clinical diagnosis.

## Run from the project root

```bash
python -m pip install -r analysis/modeling/requirements-modeling.txt
python analysis/modeling/run_depression_models.py
```

The script makes a stratified 80/20 train/test split. It compares a most-common baseline with logistic regression, decision tree, random forest, and gradient boosting. Each classifier is compared without PCA and with PCA retaining 80% of variance. Repeated stratified 5-fold cross-validation with 3 repeats is performed on the training split. The test set is held out for final evaluation.

The exact split is saved to `data/splits/depression_train_80pct.csv` and `data/splits/depression_test_20pct.csv`. Both include `Code` for row traceability, but the modeling pipeline excludes it from predictors. The target is `Higher_Beck_Score_11plus`; Beck total and Beck item answers are omitted to avoid target leakage.

To create or refresh only the split CSVs without rerunning cross-validation, use:

```bash
python analysis/modeling/run_depression_models.py --split-only
```

Imputing, encoding, scaling, and PCA are inside the pipelines and fit only on training folds. Student Code, Beck item responses, Beck total, and the supplied TMD item answers are not predictors. The model uses the recalculated phone-dependence total.

Model selection is based on mean CV PR-AUC. Test metrics use a fixed probability threshold of 0.50. The script also reports permutation importance by original input field for the model selected by CV; this indicates predictive usefulness in this dataset, not causation.

## Outputs

- `results/depression_model_comparison.csv` — CV and held-out metrics for each model/PCA variant.
- `results/depression_repeated_cv_folds.csv` — per-fold CV scores.
- `results/best_model_test_predictions.csv` — held-out predictions for the CV-selected model.
- `results/best_model_permutation_importance.csv` — input fields ranked by PR-AUC decrease when shuffled.
- `results/modeling_summary.md` — plain-language method summary and top importance values.
- `models/best_depression_model.joblib` — selected fitted pipeline, trained on the 80% split.

The 11+ cutoff is the dataset's score-band boundary and remains subject to lecturer confirmation that the scoring guide matches the questionnaire version.
