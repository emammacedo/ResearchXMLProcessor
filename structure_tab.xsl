<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<html>
			<head>
				<style>
					td, b {
					text-align: center;
					}
				</style>
			</head>
			<body>
				<h1>Current researchers (selected by the User)</h1>

				<xsl:choose>
					<xsl:when test="count(//researcher) = 0">
						<p>There is no researcher with the filters you have inserted.</p>
					</xsl:when>
					<xsl:otherwise>
						<table border="1">
							<tr>
								<td>
									<b>Name</b>
								</td>
								<td>
									<b>Phone Number</b>
								</td>
								<td>
									<b>Host Institution</b>
								</td>
								<td>
									<b>Research interests</b>
								</td>
								<td>
									<b>Publications</b>
								</td>
							</tr>
							<xsl:for-each select="//researcher">
								<tr>
									<td>
										<xsl:value-of select="name" />
									</td>
									<td>
										<xsl:value-of select="phone_number" />
									</td>
									<td>
										<xsl:value-of select="host_institution" />
									</td>
									<td>
										<xsl:for-each select="research_interest">
											<xsl:value-of select="." />
											<xsl:if test="position() &lt; last()">
												<xsl:text>, </xsl:text>
											</xsl:if>
										</xsl:for-each>
									</td>
									<td>
										<table border="1">
											<tr>
												<td>
													<b>Title</b>
												</td>
												<td>
													<b>Year</b>
												</td>
												<td>
													<b>Citations</b>
												</td>
												<td>
													<b>Other Authors</b>
												</td>
												<td>
													<b>Conference</b>
												</td>
												<td>
													<b>Publisher</b>
												</td>
												<td>
													<b>Journal</b>
												</td>
												<td>
													<b>Access</b>
												</td>
											</tr>
											<xsl:for-each select="publication">
												<tr>
													<td>
														<xsl:value-of select="title" />
													</td>
													<td>
														<xsl:value-of select="year" />
													</td>
													<td>
														<xsl:value-of select="citations" />
													</td>
													<td>
														<xsl:for-each select="other_authors">
															<xsl:value-of select="." />
															<xsl:if test="position() &lt; last()">
																<xsl:text>, </xsl:text>
															</xsl:if>
														</xsl:for-each>
													</td>
													<td>
														<xsl:value-of select="conference" />
													</td>
													<td>
														<xsl:value-of select="publisher" />
													</td>
													<td>
														<xsl:value-of select="journal" />
													</td>
													<td>
														<xsl:value-of select="acess" />
													</td>
												</tr>
											</xsl:for-each>
										</table>
									</td>
								</tr>
							</xsl:for-each>
						</table>
						<h1>Some statistics</h1>
						<table border="1">
							<tr>
								<td>
									<b>Total number of researchers</b>
								</td>
								<td>
									<b>Total number of journals/conferences</b>
								</td>
								<td>
									<b>Total number of citations</b>
								</td>
								<td>
									<b>Top publications (maximum of 3)</b>
								</td>
								<td>
									<b>Highest cited author</b>
								</td>
							</tr>
							<xsl:for-each select="//statistics">
								<tr>
									<td>
										<xsl:value-of select="no_researchers" />
									</td>
									<td>
										<xsl:value-of select="no_journals_conferences" />
									</td>
									<td>
										<xsl:value-of select="total_citations" />
									</td>
									<td>
										<xsl:for-each select="top">
											<p>
												<xsl:number format="1. " />
												<xsl:value-of select="." />
											</p>
										</xsl:for-each>
									</td>
									<td>
										<xsl:value-of select="highest_cited_author" />
									</td>
								</tr>
							</xsl:for-each>
						</table>
					</xsl:otherwise>
				</xsl:choose>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>