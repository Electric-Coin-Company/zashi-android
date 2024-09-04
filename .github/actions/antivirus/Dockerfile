FROM clamav/clamav:1.4.0
RUN freshclam
ENTRYPOINT ["clamscan", "--recursive"]
