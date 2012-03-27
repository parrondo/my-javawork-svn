package demo;

public class DemoDescription
{
  private String className;
  private String description;

  public DemoDescription(String paramString1, String paramString2)
  {
    this.className = paramString1;
    this.description = paramString2;
  }

  public String getClassName()
  {
    return this.className;
  }

  public String getDescription()
  {
    return this.description;
  }

  public String toString()
  {
    return this.description;
  }
}