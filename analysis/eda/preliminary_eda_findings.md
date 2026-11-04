# Depression-focused exploratory data analysis

## Scope and method

This describes the raw questionnaire data. Beck total is the continuous outcome; the 11+ flag is provisional and used only for group summaries. No causation or statistical significance is claimed. Source workbooks are unchanged. Beck answer items are excluded as predictors because they define the outcome. Candidate predictors include background, family and phone-use responses, app-selection flags, and the 22 phone-dependence items/recomputed total. Ergonomics and anxiety fields are outside the current depression focus.

## Findings

- The three files contain 1,220 coded students with unique, aligned IDs and no blank cells in coded records.
- Beck totals range 0-48; mean 5.49, median 3. 455 students (37.3%) scored zero.
- At provisional 11+, 218 students (17.9%) are above cutoff. This imbalance means accuracy alone will be misleading.
- TMD and Beck totals show a weak positive association (Spearman r=0.182; Pearson r=0.132). Mean Beck by TMD band: Absence 4.33, Low 5.80, Medium 6.62, High 5.96. It does not rise steadily.
- Above-cutoff shares by phone-argument answer: Never 16.8%, Sometimes 17.9%, Often 19.6%, Always 41.4%. The Always group has only 29 students.
- Daily-use categories count <1 hour 205, 1-3 hours 1, 3-6 hours 334, >6 hours 680; only one response is in 1-3 hours.
- Age range is 16-60; 15 participants are under 18, and 129 are over 30.

## Data-quality and interpretation points

- One supplied TMD total differs from the sum of its 22 answers. Use the item-summed total for analysis and preserve the source workbook.
- Files are titled BDI-2, but several item wordings and score bands resemble the original Beck form. Item N's dictionary label conflicts with its body-image wording. Confirm the instrument and cutoff with the lecturer/supervisor.
- The dictionary says app codes are No/Yes, while the survey asks which apps are used most often. Describe these as app-selection flags until confirmed.
- Program has 19 categories and overlaps with Department's four divisions. Prefer Department in the model; retain Program for description.
- Some values exceed Tukey boxplot fences but stay within documented ranges. Do not remove valid high scores automatically.
- Repeated answer patterns exist, but student codes and full rows are unique. Do not remove students just because answers match.
- Posture-specific usage-time codebook rows have incorrect leading codes. Exclude those fields unless the scope changes.

## Presentation takeaway

“Our EDA shows that Beck scores cluster at the low end, with 17.9% at or above the provisional threshold. Phone-dependence scores show a weak positive pattern with Beck scores. Some groups show higher observed rates, but small groups and questionnaire data make the patterns exploratory. We are checking score definitions and coding before cleaning and modeling.”

## Next step

Review the quality table with the lecturer/supervisor and agree on the Beck form and cutoff. Then finalize cleaning decisions. After EDA and cleaning, prepare the depression-only modeling table, split data, and fit encoding, scaling, and PCA within training folds.

See the tables folder for the inventory, score bands, candidate feature profiles, group summaries, descriptive correlations, and quality review.
