# Saved modeling split

- Training file: `data/splits/depression_train_80pct.csv` (976 records; 174 11+ records).
- Test file: `data/splits/depression_test_20pct.csv` (244 records; 44 11+ records).
- Split: stratified 80/20, random seed 42.
- Target: `Higher_Beck_Score_11plus`. `Code` is included only to trace records and must not be used as a predictor.
- Beck_Total, Beck answer items, and supplied TMD item answers are not included. Model inputs include the recomputed TMD total.
- These same files correspond to the training and held-out test portions used by the modeling script.
