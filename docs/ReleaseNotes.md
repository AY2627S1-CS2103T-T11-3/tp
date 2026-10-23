## New

- Add unique person IDs for contacts.
- Create class and department groups using a common group model.
- Add a group-management panel to the user interface.
- Link students and colleagues to groups with `link`.
- Display linked guardians and students on contact cards.
- Preserve person IDs and group memberships when saving and loading data.
- Add a Help command window with usage instructions.
- Improve the CaNvUS branding and help-window experience.

## Improved from AB3

- Replace `find` with `filter`, supporting partial and case-insensitive name matching.
- Filter contacts by name, contact type, department, address and tags.
- Combine filter criteria to match all supplied conditions.
- Support contact-type abbreviations such as `stu`, `guar` and `col`.
- Allow duplicate contact details while keeping person IDs unique.

## Known limitations

- Guardian–student relationships still use a separate relationship map rather than ID-only fields on `Person`.
- `addgroup`, group-ID and student-ID filters are not yet available.
- A person currently supports only one group ID.
- Group definitions themselves are not yet saved between sessions.
- Full UI support for the new fields and workflows is still in progress.
