package com.shnoop.globequiz.customadapters;

public class LicenseListItem {

    private String m_name;
    private String m_description;
    private String m_license_name;
    private String m_license_text;
    private String m_link;
    private Boolean m_has_license_text;

    public LicenseListItem(String name, String description,
                    String licenseName, String licenseText, String link) {

        m_name = name;
        m_description = description;
        m_license_name = licenseName;
        m_license_text = licenseText;
        m_link = link;

        m_has_license_text = !(m_license_text == null);
    }

    public String getName() { return m_name; }
    public String getDescription() { return m_description; }
    public String getLicenseName() { return m_license_name; }
    public String getLicenseText() { return m_license_text; }
    public String getLink() { return m_link; }
    public Boolean hasLicenseText() { return m_has_license_text; }
}
